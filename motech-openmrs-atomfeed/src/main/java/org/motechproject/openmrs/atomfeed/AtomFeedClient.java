package org.motechproject.openmrs.atomfeed;

import org.apache.commons.lang.StringUtils;
import org.motechproject.openmrs.atomfeed.model.Feed;
import org.motechproject.openmrs.atomfeed.model.Link;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;
import com.thoughtworks.xstream.XStream;

public class AtomFeedClient {

    private final OpenMrsHttpClient client;
    private final XStream xstream;
    
    public AtomFeedClient(OpenMrsHttpClient client) {
        this.client = client;
        this.xstream = new XStream();
        xstream.alias("feed", Feed.class);
        xstream.alias("entry", Entry.class);
        xstream.alias("link", Link.class);
    }

    public void fetchNewOpenMrsEvents() {
        String feed = client.getOpenMrsAtomFeed();
        if (StringUtils.isEmpty(feed)) {
            return;
        }
        
        parseEvents(feed);
    }

    private void parseEvents(String feedXml) {
        Feed feed = (Feed) xstream.fromXML(feedXml);
        feed.getClass();
    }

}
