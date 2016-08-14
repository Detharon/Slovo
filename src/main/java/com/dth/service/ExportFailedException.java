package com.dth.service;

public class ExportFailedException extends Exception {

    public ExportFailedException() {
        super();
    }

    public ExportFailedException(String message) {
        super(message);
    }

    public ExportFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportFailedException(Throwable cause) {
        super(cause);
    }
}
