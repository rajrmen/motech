package org.motechproject.server.messagecampaign.web.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.server.messagecampaign.web.util.RestDateDeserializer;
import org.motechproject.server.messagecampaign.web.util.RestDateSerializer;
import org.motechproject.server.messagecampaign.web.util.RestTimeSerializer;

import java.io.Serializable;

public class SubscriptionRequest implements Serializable {

    private static final long serialVersionUID = 8082316095036755730L;

    @JsonProperty
    @JsonSerialize(using = RestTimeSerializer.class)
    private Time startTime;

    @JsonProperty
    @JsonDeserialize(using = RestDateDeserializer.class)
    @JsonSerialize(using = RestDateSerializer.class)
    private LocalDate referenceDate;

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public SubscriptionRequest(Time startTime, LocalDate referenceDate) {
        this.startTime = startTime;
        this.referenceDate = referenceDate;
    }

    public SubscriptionRequest() {
        this(null, null);
    }
}
