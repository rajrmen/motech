package org.motechproject.ivr;

import org.junit.Test;
import org.motechproject.ivr.domain.FlowSession;
import org.motechproject.ivr.domain.FlowSessionImpl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class CallSessionRecordTest {

    @Test
    public void shouldBeEqualIfSessionIdsAreSame() {
        FlowSession flowSession1 = new FlowSessionImpl("1234");
        FlowSession flowSession2 = new FlowSessionImpl("1234");
        FlowSession flowSession3 = new FlowSessionImpl("4567");

        assertThat(flowSession1, is(equalTo(flowSession2)));
        assertThat(flowSession1, is(not(equalTo(flowSession3))));
    }

    @Test
    public void shouldHoldKeyValuePairs() {
        FlowSession flowSession1 = new FlowSessionImpl("1234");
        flowSession1.add("key", "value");

        assertThat("value", is(flowSession1.valueFor("key")));
        assertNull(flowSession1.valueFor("invalid-key"));
    }
}
