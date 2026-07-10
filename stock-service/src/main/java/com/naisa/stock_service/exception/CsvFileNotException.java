package com.naisa.stock_service.exception;

public class CsvFileNotException extends RuntimeException {

    public CsvFileNotException(String message) {
        super(message);
    }

    public CsvFileNotException(String message, Throwable cause) {
        super(message, cause);
    }
}
