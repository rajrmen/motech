package org.motechproject.server.messagecampaign.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class MessageCampaignController {

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Autowired
    private CampaignEnrollmentService campaignEnrollmentService;

    @RequestMapping("/campaigns/all")
    @ResponseBody
    public String findAllCampaigns() throws IOException {
        List<Campaign> result = messageCampaignService.findAllCampaigns();
        return new ObjectMapper().writeValueAsString(result);
    }

    @RequestMapping("/enrollments/all")
    @ResponseBody
    public String findAllEnrollments() throws IOException {
        List<CampaignEnrollment> result = campaignEnrollmentService.findAllEnrollments();
        return new ObjectMapper().writeValueAsString(result);
    }
}
