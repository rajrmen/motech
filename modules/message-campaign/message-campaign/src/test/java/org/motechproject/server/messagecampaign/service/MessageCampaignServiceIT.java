package org.motechproject.server.messagecampaign.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class MessageCampaignServiceIT {

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Test
    public void testFindAllCampaigns() {
        List<Campaign> campaigns = messageCampaignService.findAllCampaigns();
        assertNotNull(campaigns);
        assertTrue(campaigns.size() > 0);
    }
}
