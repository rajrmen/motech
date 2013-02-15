package org.motechproject.server.messagecampaign.domain.campaign;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.List;

@TypeDiscriminator("doc.type === 'Campaign'")
public abstract class Campaign<T extends CampaignMessage> extends MotechBaseDataObject {

    @JsonProperty
    private String name;

    @JsonProperty
    private List<T> messages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessages(List<T> messages) {
        this.messages = messages;
    }

    public List<T> getMessages() {
        return messages;
    }
}
