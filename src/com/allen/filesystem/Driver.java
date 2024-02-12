package com.allen.filesystem;

import com.allen.filesystem.blockdevice.Block;
import com.allen.filesystem.exceptions.FileNotFoundException;
import com.allen.filesystem.exceptions.FileOpenModeNotSupported;
import com.allen.filesystem.exceptions.FileSystemFulllException;
import com.allen.filesystem.filesystem.FOpenModes;
import com.allen.filesystem.filesystem.FileSystem;
import com.allen.filesystem.filesystem.FileSystemFactory;

public class Driver {
    public static void main(String[] args) {
        FileSystem fs = FileSystemFactory.getFileSystem(10, 100);
        // This should throw exception
        try {
            fs.fOpen("file1", FOpenModes.READ);
        } catch (Exception e) {
            System.out.println(e);
        }

        // This
        try {
            String filename = "file1";
            Block block = fs.fOpen(filename, FOpenModes.WRITE);
            String content = "This is the first file we are creating in the filesystem!";
            fs.fWrite(block, content);
            fs.fClose(filename);

            Block readBlock = fs.fOpen(filename, FOpenModes.READ);
            String readData = fs.fRead(readBlock);
            fs.fClose(filename);
            System.out.println(readData);

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
