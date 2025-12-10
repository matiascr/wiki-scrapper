package com.lastminute.recruitment.client;

import com.lastminute.recruitment.client.error.WikiPageHtmlParseException;
import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;


/**
 * Fetches pages in HTML and parses them accordingly to return a WikiPage.
 */
public class HtmlWikiClient implements WikiClient {
    private static final String EXPECTED_FILE_FORMAT = ".html";

    private final ResourceLoader resourceLoader;

    public HtmlWikiClient(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Optional<WikiPage> get(String link) throws WikiPageNotFound, WikiPageHtmlParseException {
        var parsedLink = parseLink(link);

        Resource resource;
        try {
            resource = resourceLoader.getResource(RESOURCE_PATH + parsedLink);
            if (!resource.exists()) {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new WikiPageNotFound("Linked wiki page at " + link + " could not be found", e);
        }

        try {
            String resourceHtml = resource.getContentAsString(StandardCharsets.UTF_8);
            WikiPage wikiPage = parseHtml(resourceHtml);
            return Optional.of(wikiPage);
        } catch (Exception e) {
            throw new WikiPageHtmlParseException("Failed to parse HTML page at " + link, e);
        }
    }

    private WikiPage parseHtml(String resourceHtml) {
        Document document = Jsoup.parse(resourceHtml);

        // Parse the HTML head
        var head = document.head();

        String selfLink = null;
        Optional<Element> selfLinkElement = head.getElementsByTag("meta").stream().filter(element -> element.hasAttr("selfLink")).findFirst();
        if (selfLinkElement.isPresent()) {
            selfLink = selfLinkElement.get().attr("selfLink").trim();
        }

        String title = null;
        Optional<Element> titleElement = head.getElementsByTag("title").stream().findFirst();
        if (titleElement.isPresent()) {
            title = titleElement.get().val().trim();
        }

        head.getElementsByTag("meta");

        // Parse the HTML body
        var body = document.body();

        String content = null;
        var contentElement = body.getElementsByTag("p").stream().filter(element -> element.classNames().contains("content")).findFirst();
        if (contentElement.isPresent()) {
            content = contentElement.get().val();
        }

        ArrayList<String> internalLinks = new ArrayList<>();
        var listElement = body.getElementsByTag("ul").stream().findFirst();
        if (listElement.isPresent()) {
            var internalLinkElements = listElement.get().getElementsByTag("li");
            for (var internalLinkElement : internalLinkElements) {
                var internalLinkElementLink = internalLinkElement.getElementsByTag("a").stream().findFirst();
                if (internalLinkElementLink.isPresent()) {
                    var href = internalLinkElementLink.get().attr("href");
                    internalLinks.add(href);
                }
            }
        }

        return new WikiPage(title, content, selfLink, internalLinks);
    }

    private String parseLink(String link) {
        String trimmedLink = link.trim();
        String subLink = trimmedLink;

        if (trimmedLink.startsWith(WIKI_SCRAPPER_TEST_URL)) {
            subLink = trimmedLink.replaceFirst(WIKI_SCRAPPER_TEST_URL, "");
        }

        return subLink + EXPECTED_FILE_FORMAT;
    }
}

