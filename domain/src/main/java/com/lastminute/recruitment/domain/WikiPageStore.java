package com.lastminute.recruitment.domain;

import java.util.Optional;


/**
 * According to the README:
 * "You don't have to worry about DB part, it's already implemented."
 * <p>
 * This is probably how I would define the interface.
 */
public interface WikiPageStore {
    void save(WikiPage wikiPage);

    Optional<WikiPage> get(String link);

    boolean contains(String link);

    boolean contains(WikiPage wikiPage);
}
