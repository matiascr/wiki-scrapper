package com.lastminute.recruitment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class WikiScrapperTest {
    /*
    The page structure for the tests
              root
              /  \
             1    2
            /    / \
           2    1   3
     With it we can test:
     - No children
     - One children
     - Multiple children
     - Loops
     */
    private final WikiPage root = new WikiPage("root", "", "root", List.of("page1", "page2"));
    private final WikiPage page1 = new WikiPage("page1", "", "page1", List.of("page2"));
    private final WikiPage page2 = new WikiPage("page2", "", "page2", List.of("page1", "page3"));
    private final WikiPage page3 = new WikiPage("page3", "", "page3", List.of());
    private final List<WikiPage> allPages = List.of(root, page1, page2, page3);

    private WikiClient wikiClient;
    private WikiScrapper wikiScrapper;
    private WikiPageStore wikiPageStore;
    /**
     * Stand-in for the DB. A simple key-value store.
     */
    private HashMap<String, WikiPage> wikiPageStoreDummy;

    @BeforeEach
    void setup() {
        wikiClient = mock(WikiClient.class);
        wikiPageStore = mock(WikiPageStore.class);
        wikiPageStoreDummy = new HashMap<>();
        wikiScrapper = new WikiScrapper(wikiClient, wikiPageStore);

        // Setup for the dummy DB
        for (var wikiPage : allPages) {
            var link = wikiPage.getSelfLink();

            // When a page gets added to the DB, add to a map.
            doAnswer(invocation -> {
                WikiPage p = invocation.getArgument(0);
                wikiPageStoreDummy.put(p.getSelfLink(), p);
                return null;
            }).when(wikiPageStore).save(wikiPage);

            // When a page is retrieved from the DB, get from the map.
            doAnswer(invocation -> {
                String arg = invocation.getArgument(0);
                return Optional.ofNullable(wikiPageStoreDummy.get(arg));
            }).when(wikiPageStore).get(link);

            doAnswer(invocation -> {
                String arg = invocation.getArgument(0);
                return wikiPageStoreDummy.containsKey(arg);
            }).when(wikiPageStore).contains(link);

            doAnswer(invocation -> {
                WikiPage arg = invocation.getArgument(0);
                return wikiPageStoreDummy.containsKey(arg.getSelfLink());
            }).when(wikiPageStore).contains(wikiPage);
        }
    }

    /**
     * Page does NOT exist
     * Page is NOT stored
     * ---
     * Page is NOT returned by client
     * Page is NOT saved to DB
     * Page is NOT returned by scrapper
     */
    @Test
    void when_failClientGet_should_failScrapperRead() {
        String link = "missing-page";
        when(wikiClient.get(link)).thenReturn(Optional.empty());
        when(wikiPageStore.get(link)).thenReturn(Optional.empty());
        when(wikiPageStore.contains(link)).thenReturn(false);

        Optional<WikiPage> result = wikiScrapper.read(link);

        assertTrue(result.isEmpty());
        verify(wikiPageStore, never()).save(any());
    }

    /**
     * Page exists
     * Page has no children
     * Page is NOT stored
     * --- then ---
     * Page is returned by client
     * Page is saved to DB
     * Page is returned by scrapper
     */
    @Test
    void when_successClientGet_failStoreGet_should_storeSave_successScrapperRead() {
        var page = page3;
        var link = page3.getSelfLink();

        when(wikiClient.get(link)).thenReturn(Optional.of(page));
        when(wikiPageStore.get(link)).thenReturn(Optional.empty());
        when(wikiPageStore.contains(page)).thenReturn(false);
        when(wikiPageStore.contains(link)).thenReturn(false);

        Optional<WikiPage> result = wikiScrapper.read(link);

        verify(wikiClient).get(link);
        verify(wikiPageStore, times(1)).save(page);
        assertTrue(result.isPresent());
    }

    /**
     * Page exists
     * Page has no children
     * Page is already in DB
     * --- then ---
     * Page is NOT searched by client
     * Page is NOT saved to DB
     * Page is returned by scrapper
     */
    @Test
    void when_successClientGet_successStoreGet_should_notSave_successScrapperRead() {
        var page = page3;
        var link = page3.getSelfLink();

        when(wikiClient.get(link)).thenReturn(Optional.of(page));
        when(wikiPageStore.get(link)).thenReturn(Optional.of(page));
        when(wikiPageStore.contains(page)).thenReturn(true);
        when(wikiPageStore.contains(link)).thenReturn(true);

        Optional<WikiPage> result = wikiScrapper.read(link);

        // Client never needs to get link because it's already in DB
        verify(wikiClient, never()).get(link);
        // Store never saves page because it already exists
        verify(wikiPageStore, never()).save(page);
        // Scrapper returns the page even if it didn't need to get it from the client
        assertTrue(result.isPresent());
    }


    /**
     * Page exists
     * Page has children
     * --- then ---
     * Each page is store only ONCE
     * Pages are NOT searched in client if they are already stored. Which means:
     * - Each page is searched by client only ONCE
     * Each page is saved only ONCE
     */
    @Test
    void when_successClientGet_should_savePagesOnlyOnce_notGetPagesAlreadyInDb() {
        for (var wikiPage : allPages) {
            var link = wikiPage.getSelfLink();

            // The client always returns a link because it exists
            when(wikiClient.get(link)).thenReturn(Optional.of(wikiPage));
        }

        // DB is empty to start
        assertTrue(wikiPageStoreDummy.isEmpty());

        wikiScrapper.read(root.getSelfLink());

        for (var wikiPage : allPages) {
            // Verify each page is only needed to retrieve once by the client
            verify(wikiClient, times(1)).get(wikiPage.getSelfLink());
            // Verify pages are stored only once
            verify(wikiPageStore, times(1)).save(wikiPage);
        }

        // Verify all pages have been stored in the map
        var allLinks = allPages.stream().map(WikiPage::getSelfLink).toList();
        assertTrue(wikiPageStoreDummy.keySet().containsAll(allLinks));
    }

    @Test
    void doesNotSaveAlreadyStoredPage() {
        var alreadyPresentPage = page3;
        wikiPageStoreDummy.put(alreadyPresentPage.getSelfLink(), alreadyPresentPage);

        wikiScrapper.read(root.getSelfLink());

        verify(wikiClient, never()).get(alreadyPresentPage.getSelfLink());
        verify(wikiPageStore, never()).get(alreadyPresentPage.getSelfLink());
    }
}
