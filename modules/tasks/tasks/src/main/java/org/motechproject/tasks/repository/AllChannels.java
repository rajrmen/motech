package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_id", map = "function(doc) { if(doc.type === 'Channel') emit(doc.id); }")
public class AllChannels extends MotechBaseRepository<Channel> {

    @Autowired
    public AllChannels(final CouchDbConnector connector) {
        super(Channel.class, connector);
    }

    public void addOrUpdate(Channel channel) throws BusinessIdNotUniqueException{
        addOrReplace(channel, "id", channel.getId());
    }

    @View(name = "by_displayName", map = "function(doc) { if(doc.type === 'Channel') emit(doc.displayName); }")
    public Channel ByDisplayName(final String displayName) {
        List<Channel> channels = queryView("by_displayName", displayName);
        return channels.isEmpty() ? null : channels.get(0);
    }

}
