package com.lastminute.recruitment.domain;

import java.util.Optional;


public interface WikiPageStore {
    void save(WikiPage wikiPage);

    Optional<WikiPage> get(String link);

    boolean contains(String link);

    boolean contains(WikiPage link);
}
