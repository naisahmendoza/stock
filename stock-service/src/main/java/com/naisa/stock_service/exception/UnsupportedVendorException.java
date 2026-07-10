package com.naisa.stock_service.exception;

public class UnsupportedVendorException extends RuntimeException {

    public UnsupportedVendorException(String message) {
        super(message);
    }

    public UnsupportedVendorException(String message, Throwable cause) {
        super(message, cause);
    }
}
