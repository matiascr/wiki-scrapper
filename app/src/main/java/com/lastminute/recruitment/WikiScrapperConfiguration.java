package com.lastminute.recruitment;

import com.lastminute.recruitment.client.HtmlWikiClient;
import com.lastminute.recruitment.client.JsonWikiClient;
import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPageStore;
import com.lastminute.recruitment.domain.WikiScrapper;
import com.lastminute.recruitment.persistence.WikiPageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;


@Configuration
public class WikiScrapperConfiguration {

    @Bean
    @Profile("json")
    public WikiClient jsonWikiReader(ResourceLoader resourceLoader) {
        return new JsonWikiClient(resourceLoader);
    }

    @Bean
    @Profile("html")
    public WikiClient htmlWikiReader(ResourceLoader resourceLoader) {
        return new HtmlWikiClient(resourceLoader);
    }

    @Bean
    public WikiPageStore wikiPageRepository() {
        return new WikiPageRepository();
    }

    @Bean
    public WikiScrapper wikiScrapper(WikiClient wikiClient, WikiPageStore wikiPageRepository) {
        return new WikiScrapper(wikiClient, wikiPageRepository);
    }
}
