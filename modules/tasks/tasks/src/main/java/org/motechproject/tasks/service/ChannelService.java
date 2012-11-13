package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Channel;

import java.io.InputStream;
import java.util.List;

public interface ChannelService {

    void registerChannel(Channel channel);

    void registerChannel(InputStream channelAsJson);

    List<Channel> getAllChannels();

}
