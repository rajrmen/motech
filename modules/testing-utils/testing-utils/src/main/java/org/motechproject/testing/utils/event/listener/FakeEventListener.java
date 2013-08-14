package org.motechproject.testing.utils.event.listener;

import org.motechproject.event.listener.annotations.MotechListener;

public class FakeEventListener {
    
    public static final String SUBJECT_FOR_ONE_LISTENER_A = "subject-for-one-listener-a";
    public static final String SUBJECT_FOR_ONE_LISTENER_B = "subject-for-one-listener-b";
    public static final String SUBJECT_FOR_TWO_LISTENERS = "subject-for-two-listeners";
    
    @MotechListener(subjects = { SUBJECT_FOR_ONE_LISTENER_A, SUBJECT_FOR_ONE_LISTENER_B })
    public void handleEventsAB() {
        
    }
    
    @MotechListener(subjects = { SUBJECT_FOR_TWO_LISTENERS })
    public void handleEventC() {
        
    }
    
    @MotechListener(subjects = { SUBJECT_FOR_TWO_LISTENERS })
    public void handleEventCAlso() {
        
    }
}
