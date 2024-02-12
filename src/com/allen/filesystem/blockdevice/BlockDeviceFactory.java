package com.allen.filesystem.blockdevice;

public class BlockDeviceFactory {
    public static BlockDevice getBlockDevice(int poolSize, int blockSize) {
        return new BlockDeviceImpl(poolSize, blockSize);
    }
}
