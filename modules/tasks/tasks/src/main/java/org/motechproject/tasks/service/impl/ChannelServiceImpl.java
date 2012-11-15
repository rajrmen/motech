package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.service.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

@Service("channelService")
public class ChannelServiceImpl implements ChannelService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AllChannels allChannels;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public ChannelServiceImpl(final AllChannels allChannels) {
        this.allChannels = allChannels;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public void registerChannel(final Channel channel) {
        try {
            allChannels.addOrUpdate(channel);
            logger.info(String.format("Saved channel: %s", channel.getDisplayName()));
        } catch (BusinessIdNotUniqueException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void registerChannel(final InputStream stream) {
        Type type = new TypeToken<Channel>() {}.getType();
        Channel channel = (Channel) motechJsonReader.readFromStream(stream, type);

        registerChannel(channel);
    }

    @Override
    public List<Channel> getAllChannels() {
        return allChannels.getAll();
    }

    @Override
    public Channel getChannel(final String displayName) {
        return allChannels.ByDisplayName(displayName);
    }
}
