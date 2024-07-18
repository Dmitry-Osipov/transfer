package org.microservice.transfer.error;

public class TransferServiceException extends RuntimeException {
    public TransferServiceException(Throwable cause) {
        super(cause);
    }
}
