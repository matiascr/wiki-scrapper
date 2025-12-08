package com.lastminute.recruitment.domain;

import com.lastminute.recruitment.domain.error.WikiPageNotFound;

public class WikiScrapper {

    private final WikiClient wikiClient;
    private final WikiPageStore wikiPageStore;

    public WikiScrapper(WikiClient wikiClient, WikiPageStore wikiPageStore) {
        this.wikiClient = wikiClient;
        this.wikiPageStore = wikiPageStore;
    }

    public void read(String link) throws WikiPageNotFound {
        System.out.println("Searching link -> " + link);
    }
}
