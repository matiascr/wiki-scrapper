package com.lastminute.recruitment.client.error;

import com.lastminute.recruitment.domain.error.WikiPageParseException;


public class WikiPageJsonParseException extends WikiPageParseException {
    public WikiPageJsonParseException(String message) {
        super(message);
    }

    public WikiPageJsonParseException(String message, Exception reason) {
        super(message, reason);
    }
}
