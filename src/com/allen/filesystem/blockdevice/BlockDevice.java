package com.allen.filesystem.blockdevice;

import com.allen.filesystem.exceptions.DeviceFullException;
import com.allen.filesystem.exceptions.NotEnoughSpaceException;
import com.allen.filesystem.exceptions.UnknownBlockException;

import java.util.List;

public interface BlockDevice {
    public Block getFreeBlock() throws DeviceFullException;

    public void releaseBlock(Block block) throws UnknownBlockException;

    public void releaseBlocks(Block start, Block end) throws UnknownBlockException;

    public List<Block> allocateBlock(Block start, int length) throws UnknownBlockException, NotEnoughSpaceException;

    public boolean isFull();

    public int availableSize();

}
