package com.lastminute.recruitment.domain;


import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import com.lastminute.recruitment.domain.error.WikiPageParseException;

import java.util.Optional;


/**
 * A client can fetch and parse a page given a link.
 */
public interface WikiClient {
    String WIKI_SCRAPPER_TEST_URL = "http://wikiscrapper.test/";
    String RESOURCE_PATH = "classpath:wikiscrapper/";

    /**
     * Given a link, it fetches the linked resource (if it exists) and returns a WikiPage from it.
     *
     * @param link The link where the resource is expected to be.
     * @return an optional WikiPage if the resource exists.
     */
    Optional<WikiPage> get(String link) throws WikiPageNotFound, WikiPageParseException;
}
