package org.banktransaction.exception;

public class FileCanNotBeParsedException extends RuntimeException{
    public FileCanNotBeParsedException(String message) {
        super(message);
    }
}
