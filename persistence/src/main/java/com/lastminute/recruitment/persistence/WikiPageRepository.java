package com.lastminute.recruitment.persistence;

import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.WikiPageStore;

import java.util.HashMap;
import java.util.Optional;


/**
 * According to the README:
 * "You don't have to worry about DB part, it's already implemented."
 * <p>
 * The following is a simple key-value store.
 */
public class WikiPageRepository implements WikiPageStore {
    private final HashMap<String, WikiPage> DB = new HashMap<>();

    @Override
    public void save(WikiPage wikiPage) {
        DB.put(wikiPage.getSelfLink(), wikiPage);
    }

    @Override
    public Optional<WikiPage> get(String link) {
        return Optional.ofNullable(DB.get(link));
    }

    @Override
    public boolean contains(String link) {
        return DB.containsKey(link);
    }

    @Override
    public boolean contains(WikiPage wikiPage) {
        return DB.containsValue(wikiPage);
    }
}
