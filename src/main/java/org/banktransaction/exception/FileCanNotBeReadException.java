package org.banktransaction.exception;

public class FileCanNotBeReadException extends RuntimeException {
    public FileCanNotBeReadException(String message) {
        super(message);
    }
}
