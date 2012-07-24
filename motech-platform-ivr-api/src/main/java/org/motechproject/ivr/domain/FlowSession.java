package org.motechproject.ivr.domain;


import java.io.Serializable;

public interface FlowSession {
    public String getSessionId();
    public <T extends Serializable> T valueFor(String key);
    public <T extends Serializable> void add(String key, T value);
}
