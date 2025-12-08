package com.lastminute.recruitment.domain;


import java.util.Optional;


public interface WikiClient {
    Optional<WikiPage> get(String link);
}
