package com.allen.filesystem.filesystem;

public class FileSystemFactory {
    public static FileSystem getFileSystem(int blockSize, int blockPoolSize) {
        return new FileSystemImpl(blockSize, blockPoolSize);
    }
}
