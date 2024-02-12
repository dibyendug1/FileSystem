package com.allen.filesystem.exceptions;

public class FileSystemException extends Exception {
    public FileSystemException(String msg, Throwable th) {
        super(msg, th);
    }
}
