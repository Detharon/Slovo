package com.dth.service.transfer;

public class TransferFailedException extends Exception {

    public TransferFailedException() {
        super();
    }

    public TransferFailedException(String message) {
        super(message);
    }

    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferFailedException(Throwable cause) {
        super(cause);
    }
}
