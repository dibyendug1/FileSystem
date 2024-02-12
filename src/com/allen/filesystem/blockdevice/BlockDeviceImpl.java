package com.allen.filesystem.blockdevice;

import com.allen.filesystem.exceptions.DeviceFullException;
import com.allen.filesystem.exceptions.NotEnoughSpaceException;
import com.allen.filesystem.exceptions.UnknownBlockException;

import java.util.ArrayList;
import java.util.List;

public class BlockDeviceImpl implements BlockDevice {
    private int poolSize;
    private int blockSize;
    private Block head;
    private Block lastUpdated;
    private int currentUsedSize;

    public BlockDeviceImpl(int poolSize, int blockSize) {
        // Pool size validation logic. Pool size can't be 0
        this.poolSize = poolSize;
        this.blockSize = blockSize;
    }

    @Override
    public Block getFreeBlock() throws DeviceFullException {
        Block block = new Block(blockSize);
        // if the pool is empty
        if (this.head == null) {
            this.head = block;
            this.currentUsedSize++;
            this.lastUpdated = this.head;
            return block;
        }
        // if the pool is full, assuming fragmented blocks are not tracked
        if (currentUsedSize == poolSize) {
            throw new DeviceFullException("The device is full!!!");
        }

        if (lastUpdated != null) {
            lastUpdated.setNextNode(block);
        }

        this.currentUsedSize++;
        return block;
    }

    @Override
    public void releaseBlock(Block block) throws UnknownBlockException {
        if(block == null) {
            throw new UnknownBlockException("Block: null is unknown");
        }
        //check for reference update
        if (this.head.equals(block)) {
            this.head = block.getNextNode();
            block.setNextNode(null);
            this.head.setPrevNode(null);
        }

        if (this.lastUpdated.equals(block)) {
            this.lastUpdated = block.getPrevNode();
            block.setPrevNode(null);
            this.lastUpdated.setNextNode(null);
        }

        block.getPrevNode().setNextNode(block.getNextNode());
        block.getNextNode().setPrevNode(block.getPrevNode());

        block.setPrevNode(null);
        block.setNextNode(null);
    }

    @Override
    public void releaseBlocks(Block start, Block end) throws UnknownBlockException {
        if(start == null || end == null) {
            throw new UnknownBlockException("Block: null is unknown");
        }
        Block currNode = start;
        Block nextNode = start.getNextNode();
        Block nextToEnd = end.getNextNode();
        while (!nextNode.equals(nextToEnd)) {
            releaseBlock(currNode);
            currNode = nextNode;
            nextNode = nextNode.getNextNode();
        }
    }

    @Override
    public List<Block> allocateBlock(Block start, int length) throws UnknownBlockException, NotEnoughSpaceException {
        if (start == null) {
            throw new UnknownBlockException("Unknown block:null is provided.");
        }
        if (this.currentUsedSize + length > this.poolSize) {
            throw new NotEnoughSpaceException("Not enough space is present in the device");
        }

        List<Block> result = new ArrayList<>();
        result.add(start);
        Block node = start;
        for (int i = 1; i < length; i++) {
            Block newNode = new Block(blockSize);
            node.setNextNode(newNode);
            newNode.setPrevNode(node);
            node = newNode;
            result.add(newNode);
            this.currentUsedSize++;
        }
        this.lastUpdated = node;
        return result;
    }

    @Override
    public boolean isFull() {
        return this.currentUsedSize == this.poolSize;
    }

    @Override
    public int availableSize() {
        return this.poolSize - this.currentUsedSize;
    }
}
