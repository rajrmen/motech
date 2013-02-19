package org.motechproject.server.messagecampaign.web.controller;

import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.server.messagecampaign.web.model.CampaignDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "campaigns")
public class CampaignController {

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    @Autowired
    private MessageCampaignService messageCampaignService;

    @RequestMapping(value = "/{campaignName}", method = RequestMethod.GET)
    public @ResponseBody
    CampaignDto getCampaign(@PathVariable String campaignName) {
        CampaignRecord campaignRecord = allMessageCampaigns.findFirstByName(campaignName);

        if (campaignRecord == null) {
            throw new CampaignNotFoundException("Campaign not found: " + campaignName);
        }

        return new CampaignDto(campaignRecord);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void createCampaign(@RequestBody CampaignDto campaign) {
        CampaignRecord campaignRecord = campaign.toCampaignRecord();
        allMessageCampaigns.add(campaignRecord);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<CampaignDto> getAllCampaigns() {
        List<CampaignRecord> campaignRecords = allMessageCampaigns.getAll();

        List<CampaignDto> campaignDtos = new ArrayList<>();
        for (CampaignRecord record : campaignRecords) {
            campaignDtos.add(new CampaignDto(record));
        }

        return campaignDtos;
    }

    @RequestMapping(value = "/{campaignName}", method = RequestMethod.DELETE)
    public void deleteCampaign(@PathVariable String campaignName) {
        CampaignRecord campaign = allMessageCampaigns.findFirstByName(campaignName);

        if (campaign == null) {
            throw new CampaignNotFoundException("Campaign not found: " + campaignName);
        }

        CampaignEnrollmentsQuery enrollmentsQuery = new CampaignEnrollmentsQuery().withCampaignName(campaignName);
        messageCampaignService.stopAll(enrollmentsQuery);

        allMessageCampaigns.remove(campaign);
    }

    @ExceptionHandler(CampaignNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleException(Exception e) {
        return e.getMessage();
    }
}
