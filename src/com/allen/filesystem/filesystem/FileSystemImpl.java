package com.allen.filesystem.filesystem;

import com.allen.filesystem.blockdevice.Block;
import com.allen.filesystem.blockdevice.BlockDevice;
import com.allen.filesystem.blockdevice.BlockDeviceFactory;
import com.allen.filesystem.exceptions.*;

import java.util.List;

public class FileSystemImpl implements FileSystem {
    // Maintains metadata of the file system
    private FileSystemMetadata metadata;
    // Data layer of the file system
    private BlockDevice blockDevice;

    // Number of characters
    private int blockSize;
    // Block pool size
    private int blockPoolSize;

    public FileSystemImpl(int blockSize, int blockPoolSize) {
        this.blockSize = blockSize;
        this.blockPoolSize = blockPoolSize;
        this.metadata = new FileSystemMetadata();
        this.blockDevice = BlockDeviceFactory.getBlockDevice(blockPoolSize, blockSize);
    }

    @Override
    public Block fOpen(String filename, FOpenModes mode) throws FileSystemFulllException, FileNotFoundException, FileOpenModeNotSupported {
        // Open in write mode needs to find a block and return the block reference
        if (mode.equals(FOpenModes.WRITE)) {
            if (this.blockDevice.isFull()) {
                throw new FileSystemFulllException("Unable to create new block, the filesystem is full");
            }
            try {
                Block block = this.blockDevice.getFreeBlock();
                INode iNode = new INode(filename);
                iNode.setStart(block);
                iNode.setOpen(true);
                this.metadata.add(filename, iNode);
                return block;
            } catch (DeviceFullException e) {
                System.err.println(e);
                throw new FileSystemFulllException("Unable to create new block, the filesystem is full");
            }
        }

        // Open as read mode will return the reference from the metadata
        if (mode.equals(FOpenModes.READ)) {
            if (!this.metadata.contains(filename)) {
                throw new FileNotFoundException(String.format("File: %s is not found!!!", filename));
            }
            INode iNode = this.metadata.get(filename);
            iNode.setOpen(true);
            return iNode.getStart();
        }
        throw new FileOpenModeNotSupported(String.format("File open mode:%s is not supported", mode));
    }

    @Override
    public String fRead(Block startBlock) throws UnknownBlockException, UnsupportedFileSystemOperation {
        // TODO if the file is open for write we shouldn't allow read
        if (startBlock == null) {
            throw new UnknownBlockException("Unknown block: null provided");
        }
        INode iNode = this.metadata.get(startBlock);
        if (iNode == null) {
            throw new UnknownBlockException("Unknown block: null provided");
        }

        if (!iNode.isOpen()) {
            throw new UnsupportedFileSystemOperation("Unable to read from closed file");
        }
        StringBuilder sb = new StringBuilder();
        Block node = iNode.getStart();
        while (!node.equals(iNode.getEnd())) {
            sb.append(node.getData());
            node = node.getNextNode();
        }
        sb.append(iNode.getEnd().getData());
        return sb.toString();
    }

    @Override
    public void fWrite(Block startBlock, String content) throws UnknownBlockException, NotEnoughSpaceException, UnsupportedFileSystemOperation {
        if (startBlock == null) {
            throw new UnknownBlockException("Unknown block: null provided");
        }
        INode iNode = this.metadata.get(startBlock);
        if (iNode == null) {
            throw new UnknownBlockException("Unknown block: null provided");
        }
        if (!iNode.isOpen()) {
            throw new UnsupportedFileSystemOperation("Unable to write into closed file");
        }
        int contentSize = content.length();
        // Find number of blocks required
        int requiredBlocks = getRequiredBlocks(contentSize);
        // Find if the required number of blocks are available in the device
        if (!(this.blockDevice.availableSize() >= requiredBlocks)) {
            throw new NotEnoughSpaceException("Not enough space is available to write the content");
        }

        List<Block> blocks = this.blockDevice.allocateBlock(startBlock, requiredBlocks);
        int currentWriteSize = 0;
        for (Block block : blocks) {
            int endIndex = currentWriteSize + blockSize;
            endIndex = endIndex <= contentSize - 1 ? endIndex : contentSize - 1;
            block.setData(content.substring(currentWriteSize, endIndex));
            currentWriteSize = endIndex;
        }
        iNode.setSize(contentSize);
        iNode.setEnd(blocks.get(blocks.size() - 1));
    }

    private int getRequiredBlocks(int contentSize) {
        int multiplier = contentSize / this.blockSize;

        return this.blockSize * multiplier < contentSize ? multiplier + 1 : multiplier;
    }

    @Override
    public void fClose(String filename) throws FileNotFoundException {
        if (!this.metadata.contains(filename)) {
            throw new FileNotFoundException(String.format("File: %s is not found!!!", filename));
        }

        INode iNode = this.metadata.get(filename);
        iNode.setOpen(false);
    }

    @Override
    public void rename(String srcName, String targetName) throws FileNotFoundException {
        if (!this.metadata.contains(srcName)) {
            throw new FileNotFoundException(String.format("File: %s is not found!!!", srcName));
        }

        INode iNode = this.metadata.get(srcName);
        iNode.setFilename(targetName);
        this.metadata.remove(srcName);
        this.metadata.add(targetName, iNode);
    }

    @Override
    public void remove(String filename) throws FileNotFoundException, FileSystemException {
        if (!this.metadata.contains(filename)) {
            throw new FileNotFoundException(String.format("File: %s is not found!!!", filename));
        }

        INode iNode = this.metadata.get(filename);
        try {
            this.blockDevice.releaseBlocks(iNode.getStart(), iNode.getEnd());
        } catch (UnknownBlockException e) {
            throw new FileSystemException(String.format("Unable to remove the file: %s", filename), e);
        }

        this.metadata.remove(filename);
    }
}
