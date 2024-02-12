package com.allen.filesystem.filesystem;

import com.allen.filesystem.blockdevice.Block;

public class INode {
    private String filename;
    private Block start;
    private Block end;
    private int size;
    private boolean isOpen;

    public INode(String filename) {
        this.filename = filename;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStart(Block start) {
        this.start = start;
    }

    public void setEnd(Block end) {
        this.end = end;
    }

    public String getFilename() {
        return filename;
    }

    public Block getStart() {
        return start;
    }

    public int getSize() {
        return size;
    }

    public Block getEnd() {
        return end;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
