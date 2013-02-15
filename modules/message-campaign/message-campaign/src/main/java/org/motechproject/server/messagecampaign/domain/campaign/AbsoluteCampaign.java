package org.motechproject.server.messagecampaign.domain.campaign;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class AbsoluteCampaign extends Campaign<AbsoluteCampaignMessage> {
}
