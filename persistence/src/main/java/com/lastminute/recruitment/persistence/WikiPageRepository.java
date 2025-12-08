package com.lastminute.recruitment.persistence;

import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.WikiPageStore;

import java.util.Optional;


public class WikiPageRepository implements WikiPageStore {
    @Override
    public void save(WikiPage wikiPage) {}

    @Override
    public Optional<WikiPage> get(String link) {
        return Optional.empty();
    }

    @Override
    public boolean contains(String link) {
        return false;
    }

    @Override
    public boolean contains(WikiPage link) {
        return false;
    }
}
