package com.allen.filesystem.filesystem;

import com.allen.filesystem.blockdevice.Block;
import com.allen.filesystem.exceptions.FileNotFoundException;

import java.util.HashMap;

public class FileSystemMetadata {
    HashMap<String, INode> metadata;
    HashMap<Block, INode> startBlockToINodeMap;

    public FileSystemMetadata() {
        this.metadata = new HashMap<>();
        this.startBlockToINodeMap = new HashMap<>();
    }

    public void add(String filename, INode iNode) {
        this.metadata.put(filename, iNode);
        this.startBlockToINodeMap.put(iNode.getStart(), iNode);
    }

    public boolean contains(String filename) {
        return this.metadata.containsKey(filename);
    }

    public INode get(String filename) throws FileNotFoundException {
        if (!contains(filename)) {
            throw new FileNotFoundException(String.format("File: %s is not found!!!", filename));
        }
        return this.metadata.get(filename);
    }

    public INode get(Block start) {
        return this.startBlockToINodeMap.get(start);
    }

    public void remove(String filename) throws FileNotFoundException {
        if (!contains(filename)) {
            throw new FileNotFoundException(String.format("File: %s is not found!!!", filename));
        }
        INode iNode = this.metadata.get(filename);
        this.startBlockToINodeMap.put(iNode.getStart(), iNode);
        this.metadata.remove(filename);

    }
}
