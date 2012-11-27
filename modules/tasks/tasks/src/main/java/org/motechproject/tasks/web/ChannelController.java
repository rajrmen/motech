package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ChannelController {
    private ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(value = "channel", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getAllChannels() {
        return channelService.getAllChannels();
    }
}
