package com.naisa.stock_service.exception;

public class CsvParseException extends RuntimeException {

    public CsvParseException(String message) {
        super(message);
    }

    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
