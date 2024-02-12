package com.allen.filesystem.blockdevice;

public class Block {
    int size;
    String Data;

    Block prevNode;
    Block nextNode;

    public Block(int size) {
        // Block size validation logic. Block size can't be 0

        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getData() {
        return Data;
    }

    public void setPrevNode(Block prevNode) {
        this.prevNode = prevNode;
    }

    public void setNextNode(Block nextNode) {
        this.nextNode = nextNode;
    }

    public Block getPrevNode() {
        return prevNode;
    }

    public Block getNextNode() {
        return nextNode;
    }
}
