package org.motechproject.server.messagecampaign.web.controller;

import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "campaigns")
public class CampaignController {

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    @RequestMapping(value = "/{campaignName}")
    public void getCampaign(@PathVariable String campaignName) {
        Campaign campaign = allMessageCampaigns.get(campaignName);
    }
}
