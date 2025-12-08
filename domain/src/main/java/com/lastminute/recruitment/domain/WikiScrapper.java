package com.lastminute.recruitment.domain;

import java.util.Optional;


public class WikiScrapper {

    private final WikiClient wikiClient;
    private final WikiPageStore wikiPageStore;

    public WikiScrapper(WikiClient wikiClient, WikiPageStore wikiPageStore) {
        this.wikiClient = wikiClient;
        this.wikiPageStore = wikiPageStore;
    }

    /**
     * From a link to a wikipedia page, store it in the DB if it exists.
     * If there are any included links in the page, do the same for each of them.
     * <p>
     * If the page is already present in the DB, it is assumed to have already
     * been scrapped, so it is skipped.
     *
     * @param link The provided link.
     * @return an optional WikiPage if any was found.
     */
    public Optional<WikiPage> read(String link) {
        var previouslyStoredWikiPage = wikiPageStore.get(link);
        if (previouslyStoredWikiPage.isPresent()) {
            return previouslyStoredWikiPage;
        }

        var wikiPage = wikiClient.get(link);
        wikiPage.ifPresent(this::traverse);

        return wikiPage;
    }

    /**
     * Handles the logic for storing the WikiPage in the DB.
     * Each page can contain links to other pages, forming a tree/graph
     * structure and, thus, we "traverse" it and also handle its linked pages.
     *
     * @param wikiPage The DB to store. It's included links (if any) will be also
     * added recursively.
     */
    private void traverse(WikiPage wikiPage) {
        wikiPageStore.save(wikiPage);

        for (var includedPageLink : wikiPage.getLinks()) {
            if (!wikiPageStore.contains(includedPageLink)) {
                wikiClient.get(includedPageLink).ifPresent(this::traverse);
            }
        }
    }
}
