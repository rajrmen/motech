package org.motechproject.event.aggregation.model.rule;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class MdsExample {

    @Field
    private boolean trueFalse;

    public boolean isTrueFalse() {
        return trueFalse;
    }

    public void setTrueFalse(boolean trueFalse) {
        this.trueFalse = trueFalse;
    }
}
