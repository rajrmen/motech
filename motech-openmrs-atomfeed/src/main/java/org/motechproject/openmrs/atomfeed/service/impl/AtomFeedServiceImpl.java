package org.motechproject.openmrs.atomfeed.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.openmrs.atomfeed.ConceptEvent;
import org.motechproject.openmrs.atomfeed.OpenMrsHttpClient;
import org.motechproject.openmrs.atomfeed.PatientEvent;
import org.motechproject.openmrs.atomfeed.model.Entry;
import org.motechproject.openmrs.atomfeed.model.Feed;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;

import com.thoughtworks.xstream.XStream;

public class AtomFeedServiceImpl implements AtomFeedService {

    private final OpenMrsHttpClient client;
    private final XStream xstream;
    private final OutboundEventGateway outboundGateway;

    public AtomFeedServiceImpl(OpenMrsHttpClient client, OutboundEventGateway outboundGateway) {
        this.client = client;
        this.outboundGateway = outboundGateway;
        xstream = new XStream();
        xstream.processAnnotations(Feed.class);
        xstream.omitField(Entry.class, "summary");
    }

    /* (non-Javadoc)
     * @see org.motechproject.openmrs.atomfeed.AtomFeedClient#fetchNewOpenMrsEvents()
     */
    @Override
    public void fetchNewOpenMrsEvents() {
        String feed = client.getOpenMrsAtomFeed();
        if (StringUtils.isEmpty(feed)) {
            return;
        }

        parseEvents(feed);
    }

    private void parseEvents(String feedXml) {
        Feed feed = (Feed) xstream.fromXML(feedXml);
        List<Entry> entries = feed.getEntry();

        for (Entry entry : entries) {
            MotechEvent event = null;
            if ("org.openmrs.Patient".equals(entry.getClassname())) {
                event = handlePatientEntry(entry);
            } else if ("org.openmrs.Concept".equals(entry.getClassname())) {
                event = handleConceptEntry(entry);
            }

            outboundGateway.sendEventMessage(event);
        }
    }

    private MotechEvent handlePatientEntry(Entry entry) {
        return new PatientEvent(entry).toEvent();
    }

    private MotechEvent handleConceptEntry(Entry entry) {
        return new ConceptEvent(entry).toEvent();
    }

}
