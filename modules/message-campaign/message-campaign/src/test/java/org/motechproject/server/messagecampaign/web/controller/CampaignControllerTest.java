package org.motechproject.server.messagecampaign.web.controller;


import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.loader.CampaignJsonLoader;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class CampaignControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", Charset.forName("UTF-8"));

    private CampaignJsonLoader campaignJsonLoader = new CampaignJsonLoader();

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc controller;

    @InjectMocks
    private CampaignController campaignController = new CampaignController();

    @Mock
    private AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = MockMvcBuilders.standaloneSetup(campaignController).build();
    }

    @Test
    public void testGetCampaignDetails() throws Exception {
        final CampaignRecord campaignRecord = readSingleCampaign("campaignDetails.json");
        final String campaignName = "Absolute Dates Message Program";
        when(allMessageCampaigns.findFirstByName(campaignName)).thenReturn(campaignRecord);

        final String expectedResponse = loadJson("campaignDetails.json");

        controller.perform(
            get("/campaigns/{campaignName}", campaignName)
        ).andExpect(
            status().is(HttpStatus.OK.value())
        ).andExpect(
            content().type(APPLICATION_JSON_UTF8)
        ).andExpect(
            content().string(jsonMatcher(expectedResponse))
        );

        verify(allMessageCampaigns).findFirstByName(campaignName);
    }

    private List<CampaignRecord> readCampaigns(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/campaigns/" + filename)) {
            return campaignJsonLoader.loadCampaigns(in);
        }
    }

    private CampaignRecord readSingleCampaign(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/campaigns/" + filename)) {
            return campaignJsonLoader.loadSingleCampaign(in);
        }
    }

    private String loadJson(String filename) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("rest/campaigns/" + filename)) {
            return IOUtils.toString(in);
        }
    }

    private Matcher<String> jsonMatcher(final String expected) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object argument) {
                try {
                    String actual = (String) argument;

                    JsonNode expectedTree = objectMapper.readTree(expected);
                    JsonNode actualTree = objectMapper.readTree(actual);

                    return expectedTree.equals(actualTree);
                } catch (IOException e) {
                    throw new MotechException("Json parsing failure", e);
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expected);
            }
        };
    }
}
