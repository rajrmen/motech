package org.motechproject.mobileforms.api.callbacks;

import java.util.HashMap;
import java.util.Map;

import org.motechproject.event.EventRelay;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormPublisher {

    public static final String FORM_BEAN = "formBean";
    public static final String FORM_VALIDATION_SUCCESSFUL = "form.validation.successful";

    private EventRelay eventRelay;

    @Autowired
    public FormPublisher(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void publish(FormBean formBean) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FORM_BEAN, formBean);
        String eventSubject = FORM_VALIDATION_SUCCESSFUL + "." + formBean.getStudyName() + "." + formBean.getFormname();
        MotechEvent motechEvent = new MotechEvent(eventSubject, params);
        eventRelay.sendEventMessage(motechEvent);
    }

}
