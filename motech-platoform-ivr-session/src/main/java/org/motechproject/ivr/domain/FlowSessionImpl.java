package org.motechproject.ivr.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'FlowSession'")
public class FlowSessionImpl extends MotechBaseDataObject implements FlowSession {

    @JsonProperty
    private Map<String, Object> data;

    @JsonProperty
    private String sessionId;

    FlowSessionImpl() {
    }

    public FlowSessionImpl(String sessionId) {
        this.sessionId = sessionId;
        data = new HashMap<String, Object>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowSession that = (FlowSession) o;

        return new EqualsBuilder().append(this.getSessionId(), that.getSessionId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sessionId).toHashCode();
    }

    public <T extends Serializable> void add(String key, T value) {
        data.put(key, value);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public <T extends Serializable> T valueFor(String key) {
        return (T) data.get(key);
    }
}
