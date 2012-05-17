package org.motechproject.sms.http.web;

import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.SmsHttpTemplate;
import org.motechproject.sms.http.TemplateReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("/sms")
public class InboundSmsController {
    private static final String TEMPLATE_FILE = "/sms-http-template.json";

    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();
    private SmsHttpTemplate template;

    @Autowired
    public InboundSmsController(TemplateReader templateReader) {
        this.template = templateReader.getTemplate(TEMPLATE_FILE);
    }

    public InboundSmsController(TemplateReader templateReader, EventRelay eventRelay) {
        this(templateReader);
        this.eventRelay = eventRelay;
    }

    @RequestMapping(value = "inbound")
    public void handle(HttpServletRequest request) {
        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put(EventDataKeys.SENDER, request.getParameter(template.getIncoming().getSenderKey()));
        payload.put(EventDataKeys.INBOUND_MESSAGE, request.getParameter(template.getIncoming().getMessageKey()));
        payload.put(EventDataKeys.TIMESTAMP, request.getParameter(template.getIncoming().getTimestampKey()));
        eventRelay.sendEventMessage(new MotechEvent(EventSubjects.INBOUND_SMS, payload));
    }
}
