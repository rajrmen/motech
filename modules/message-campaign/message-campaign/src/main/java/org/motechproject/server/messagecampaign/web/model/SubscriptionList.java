package org.motechproject.server.messagecampaign.web.model;


import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionList {

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String campaignName;

    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String externalId;

    @JsonProperty
    private List<Subscription> subscriptions = new ArrayList<>();

    public SubscriptionList() {
    }

    public SubscriptionList(List<CampaignEnrollment> enrollments) {
        addEnrollments(enrollments);
    }

    public final void addEnrollments(List<CampaignEnrollment> enrollments) {
        for (CampaignEnrollment enrollment : enrollments) {
            subscriptions.add(new Subscription(enrollment));
        }
    }

    public void setCommonCampaignName(String campaignName) {
        this.campaignName = campaignName;
        for (Subscription subscription : subscriptions) {
            subscription.setCampaignName(null);
        }
    }

    public void setCommonExternalId(String externalId) {
        this.externalId = externalId;
        for (Subscription subscription : subscriptions) {
            subscription.setExternalId(null);
        }
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
