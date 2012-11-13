package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Channel;
import org.springframework.stereotype.Repository;

@Repository
public class AllChannels extends MotechBaseRepository<Channel> {

    public AllChannels(final CouchDbConnector connector) {
        super(Channel.class, connector);
    }

    public void addOrUpdate(Channel channel) throws BusinessIdNotUniqueException{
        addOrReplace(channel, "id", channel.getId());
    }

}
