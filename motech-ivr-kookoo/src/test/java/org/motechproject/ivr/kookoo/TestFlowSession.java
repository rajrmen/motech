package org.motechproject.ivr.kookoo;

import org.motechproject.ivr.domain.FlowSession;

import java.io.Serializable;

public class TestFlowSession implements FlowSession {

    private String sid;

    public TestFlowSession(String sid) {
        this.sid = sid;
    }

    @Override
    public String getSessionId() {
        return sid;
    }

    @Override
    public <T extends Serializable> T valueFor(String key) {
        return null;
    }

    @Override
    public <T extends Serializable> void add(String key, T value) {

    }
}
