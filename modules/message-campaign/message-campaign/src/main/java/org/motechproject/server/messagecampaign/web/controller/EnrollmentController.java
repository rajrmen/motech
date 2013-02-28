package org.motechproject.server.messagecampaign.web.controller;

import org.motechproject.server.messagecampaign.web.model.EnrollmentList;
import org.motechproject.server.messagecampaign.web.model.EnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/enrollments")
public class EnrollmentController {

    @Autowired
    private org.motechproject.server.messagecampaign.web.api.EnrollmentRestController enrollmentController;

    @RequestMapping(value = "/{campaignName}/users/", method = RequestMethod.POST)
    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.OK)
    public void enrollOrUpdateUser(@PathVariable String campaignName,
                                   @RequestParam String externalId,
                                   @RequestParam String enrollmentId) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setEnrollmentId(enrollmentId);
        enrollmentController.enrollOrUpdateUser(campaignName, externalId, enrollmentRequest);
    }

    /*@RequestMapping(value = "/{campaignName}/users/{userId}", method = RequestMethod.GET)
    @PreAuthorize(HAS_MANAGE_ENROLLMENTS_ROLE)
    public @ResponseBody
    EnrollmentDto getEnrollment(@PathVariable String campaignName, @PathVariable String userId) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery()
                .withCampaignName(campaignName).withExternalId(userId);

        List<CampaignEnrollment> enrollments = enrollmentService.search(query);

        if (enrollments.isEmpty()) {
            throw subscriptionsNotFoundException(userId);
        } else {
            return new EnrollmentDto(enrollments.get(0));
        }
    }*/

    /*@RequestMapping(value = "/{campaignName}/users/{userId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(HAS_MANAGE_ENROLLMENTS_ROLE)
    public void updateEnrollment(@PathVariable String campaignName, @PathVariable String userId,
                                 @RequestBody EnrollmentRequest enrollmentRequest) {
        CampaignRequest campaignRequest = new CampaignRequest(userId, campaignName,
                enrollmentRequest.getReferenceDate(), null, enrollmentRequest.getStartTime());

        messageCampaignService.stopAll(campaignRequest);
        messageCampaignService.startFor(campaignRequest);
    }*/

    @RequestMapping(value = "/{campaignName}/users", method = RequestMethod.DELETE)
    @PreAuthorize("permitAll")
    @ResponseStatus(HttpStatus.OK)
    public void removeEnrollment(@PathVariable String campaignName, @RequestParam String externalId) {
        removeEnrollment(campaignName, externalId);
    }
    
    /*@RequestMapping(value = "/{campaignName}/users", method = RequestMethod.GET)
    public @ResponseBody EnrollmentList getEnrollmentsForCampaign(@PathVariable String campaignName) {
        return enrollmentController.getEnrollmentsForCampaign(campaignName);
    }*/

   /* @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public @ResponseBody EnrollmentList getEnrollmentsForUser(@PathVariable String userId) {
        return enrollmentController.getEnrollmentsForUser(userId);
    }*/

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public @ResponseBody EnrollmentList getAllEnrollments(
            @RequestParam(required = false) String enrollmentStatus,
            @RequestParam(required = false) String externalId, @RequestParam(required = false) String campaignName) {
        return enrollmentController.getAllEnrollments(enrollmentStatus, externalId, campaignName);
    }
}

