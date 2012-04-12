package org.motechproject.adherence.dao;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.adherence.domain.AdherenceLog;

import java.util.List;

public class AdherenceRecord {

    @JsonProperty
    private String _id;

    @JsonProperty
    private int deltaDosesTaken;

    @JsonProperty
    private int deltaTotalDoses;

    public String get_id() {
        return _id;
    }

    public int getDeltaDosesTaken() {
        return deltaDosesTaken;
    }

    public int getDeltaTotalDoses() {
        return deltaTotalDoses;
    }
}
