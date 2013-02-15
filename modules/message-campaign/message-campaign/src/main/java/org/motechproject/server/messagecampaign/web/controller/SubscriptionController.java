package org.motechproject.server.messagecampaign.web.controller;

import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.web.ex.SubscriptionsNotFoundException;
import org.motechproject.server.messagecampaign.web.model.Subscription;
import org.motechproject.server.messagecampaign.web.model.SubscriptionList;
import org.motechproject.server.messagecampaign.web.model.SubscriptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "subscriptions")
public class SubscriptionController {

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Autowired
    private CampaignEnrollmentService enrollmentService;

    @RequestMapping(value = "/{campaignName}/users/{userId}", method = RequestMethod.POST)
    public void enrollUser(@PathVariable String campaignName, @PathVariable String userId,
                           @RequestBody SubscriptionRequest subscriptionRequest) {
        CampaignRequest campaignRequest = new CampaignRequest(userId, campaignName,
                subscriptionRequest.getReferenceDate(), null, subscriptionRequest.getStartTime());

        messageCampaignService.startFor(campaignRequest);
    }

    @RequestMapping(value = "/{campaignName}/users/{userId}", method = RequestMethod.GET)
    public @ResponseBody Subscription getEnrollment(@PathVariable String campaignName, @PathVariable String userId) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery()
                .withCampaignName(campaignName).withExternalId(userId);

        List<CampaignEnrollment> enrollments = enrollmentService.search(query);

        if (enrollments.isEmpty()) {
            throw subscriptionsNotFoundException(userId);
        } else {
            return new Subscription(enrollments.get(0));
        }
    }

    @RequestMapping(value = "/{campaignName}/users/{userId}", method = RequestMethod.PUT)
    public void updateSubscription(@PathVariable String campaignName, @PathVariable String userId,
                                   @RequestBody SubscriptionRequest subscriptionRequest) {
        CampaignRequest campaignRequest = new CampaignRequest(userId, campaignName,
                subscriptionRequest.getReferenceDate(), null, subscriptionRequest.getStartTime());

        messageCampaignService.stopAll(campaignRequest);
        messageCampaignService.startFor(campaignRequest);
    }

    @RequestMapping(value = "/{campaignName}/users/{externalId}", method = RequestMethod.DELETE)
    public void removeSubscription(@PathVariable String campaignName, @PathVariable String userId) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery()
                .withCampaignName(campaignName).withExternalId(userId);

        List<CampaignEnrollment> enrollments = enrollmentService.search(query);

        if (enrollments.isEmpty()) {
            throw subscriptionsNotFoundException(userId);
        } else {
            CampaignRequest campaignRequest = new CampaignRequest();
            campaignRequest.setCampaignName(campaignName);
            campaignRequest.setExternalId(userId);

            messageCampaignService.stopAll(campaignRequest);
        }
    }
    
    @RequestMapping(value = "/{campaignName}/users", method = RequestMethod.GET)
    public @ResponseBody SubscriptionList getSubscriptionsForCampaign(@PathVariable String campaignName) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery().withCampaignName(campaignName);

        List<CampaignEnrollment> enrollments = enrollmentService.search(query);

        SubscriptionList subscriptionList = new SubscriptionList(enrollments);
        subscriptionList.setCommonCampaignName(campaignName);

        return subscriptionList;
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public @ResponseBody SubscriptionList getSubscriptionsForUser(@PathVariable String userId) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery().withExternalId(userId);

        List<CampaignEnrollment> enrollments = enrollmentService.search(query);

        if (enrollments.isEmpty()) {
            throw subscriptionsNotFoundException(userId);
        } else {
            SubscriptionList subscriptionList = new SubscriptionList(enrollments);
            subscriptionList.setCommonExternalId(userId);

            return subscriptionList;
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public @ResponseBody SubscriptionList getAllSubscriptions(
            @RequestParam(required = false) String enrollmentStatus,
            @RequestParam(required = false) String externalId, @RequestParam(required = false) String campaignName) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery();

        if (enrollmentStatus != null) {
            query.havingState(CampaignEnrollmentStatus.valueOf(enrollmentStatus.toUpperCase(Locale.ENGLISH)));
        }
        if (campaignName != null) {
            query.withCampaignName(campaignName);
        }
        if (externalId != null) {
            query.withExternalId(externalId);
        }

        List<CampaignEnrollment> enrollments = enrollmentService.search(query);

        return new SubscriptionList(enrollments);
    }

    @ExceptionHandler({ SubscriptionsNotFoundException.class, CampaignNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleException(Exception e) {
        return e.getMessage();
    }

    private SubscriptionsNotFoundException subscriptionsNotFoundException(String userId) {
        return new SubscriptionsNotFoundException("No enrollments found for user " + userId);
    }
}
