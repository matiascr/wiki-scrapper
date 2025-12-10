package com.lastminute.recruitment.domain.error;

public class WikiPageParseException extends RuntimeException {
    public WikiPageParseException(String message) {
        super(message);
    }

    public WikiPageParseException(String message, Exception reason) {
        super(message, reason);
    }
}
