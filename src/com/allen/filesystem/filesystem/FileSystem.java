package com.allen.filesystem.filesystem;

import com.allen.filesystem.blockdevice.Block;
import com.allen.filesystem.exceptions.*;

/**
 *
 */
public interface FileSystem {
    /**
     * @param filename
     * @param mode
     * @return
     */
    public Block fOpen(String filename, FOpenModes mode) throws FileSystemFulllException, FileNotFoundException, FileOpenModeNotSupported;

    /**
     * @param startBlock
     * @return
     */
    public String fRead(Block startBlock) throws UnknownBlockException, UnsupportedFileSystemOperation;

    /**
     * @param startBlock
     * @param content
     */
    public void fWrite(Block startBlock, String content) throws UnknownBlockException, NotEnoughSpaceException, UnsupportedFileSystemOperation;

    /**
     *
     */
    public void fClose(String filename) throws FileNotFoundException;

    /**
     * @param srcName
     * @param targetName
     */
    public void rename(String srcName, String targetName) throws FileNotFoundException;

    /**
     * @param filename
     */
    public void remove(String filename) throws FileNotFoundException, FileSystemException;
}
