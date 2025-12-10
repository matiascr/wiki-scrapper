package com.lastminute.recruitment.client.error;

import com.lastminute.recruitment.domain.error.WikiPageParseException;


public class WikiPageHtmlParseException extends WikiPageParseException {
    public WikiPageHtmlParseException(String message) {
        super(message);
    }

    public WikiPageHtmlParseException(String message, Exception reason) {
        super(message, reason);
    }
}
