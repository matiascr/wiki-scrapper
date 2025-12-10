package com.lastminute.recruitment.client;

import com.lastminute.recruitment.client.error.WikiPageJsonParseException;
import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


/**
 * Fetches pages in HTML and parses them accordingly to return a WikiPage.
 */
public class JsonWikiClient implements WikiClient {
    private static final String EXPECTED_FILE_FORMAT = ".json";

    private final ResourceLoader resourceLoader;

    public JsonWikiClient(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
     public Optional<WikiPage> get(String link) throws WikiPageNotFound, WikiPageJsonParseException {
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
            String resourceJson = resource.getContentAsString(StandardCharsets.UTF_8);
            WikiPage wikiPage = parseJson(resourceJson);
            return Optional.of(wikiPage);
        } catch (Exception e) {
            throw new WikiPageJsonParseException("Failed to parse JSON page at " + link, e);
        }
    }

    private WikiPage parseJson(String resourceJson) {
        ObjectMapper objectMapper = JsonMapper.builder().build();
        var tree = objectMapper.readTree(resourceJson);

        var title = parseJsonStringValue(tree, "title");
        var content = parseJsonStringValue(tree, "content");
        var selfLink = parseJsonStringValue(tree, "selfLink");

        var linksNode = Optional.ofNullable(tree.get("links"));
        List<String> links = List.of();
        if (linksNode.isPresent()) {
            links = linksNode.get().values().stream().map(JsonNode::stringValue).toList();
        }

        return new WikiPage(title, content, selfLink, links);
    }

    private String parseJsonStringValue(JsonNode root, String name) {
        Optional<JsonNode> node = Optional.ofNullable(root.get(name));
        return node.map(JsonNode::stringValue).orElse(null);
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
