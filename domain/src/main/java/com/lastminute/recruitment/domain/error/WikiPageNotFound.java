package com.lastminute.recruitment.domain.error;

public class WikiPageNotFound extends RuntimeException{
    public WikiPageNotFound(String link) {
        super("Page not found: " + link);
    }
}
