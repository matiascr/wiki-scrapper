package com.lastminute.recruitment;

import com.lastminute.recruitment.rest.WikiScrapperResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WikiScrapperResource.class)
@Import(WikiScrapperConfiguration.class)
@TestPropertySource(properties = "spring.profiles.active=html")
class HtmlWikiScrapperTest {

    private static final String URL_PATH = "/wiki/scrap";
    private static final String LINK = "http://wikiscrapper.test/";

    private static final String pageName = "site1";
    private static final String pageLink = LINK + pageName;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldReturnOk_WhenLinkIsValidAndFound() throws Exception {
        mockMvc.perform(post(URL_PATH)
                        .content(pageLink))
                .andExpect(status().isOk());
    }

    @Test
    void shouldStripQuotes_AndReturnOk_WhenLinkIsSurroundedByQuotes() throws Exception {
        String linkWithQuotes = "\"" + pageLink + "\"";

        mockMvc.perform(post(URL_PATH)
                        .content(linkWithQuotes))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFound_WhenScrapperReturnsEmpty() throws Exception {
        var link = LINK + "fake_link";

        mockMvc.perform(post(URL_PATH)
                        .content(link))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Page not found: " + link));
    }

    @Test
    void shouldReturnBadRequest_WhenBodyIsMissing() throws Exception {
        mockMvc.perform(post(URL_PATH))
                .andExpect(status().isBadRequest());
    }
}