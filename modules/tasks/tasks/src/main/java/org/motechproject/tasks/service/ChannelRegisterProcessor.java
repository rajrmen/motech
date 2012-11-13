package org.motechproject.tasks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

public class ChannelRegisterProcessor {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ChannelService channelService;
    private ResourceLoader resourceLoader;

    public void registerChannel(final String filePath) {
        if (channelService != null) {
            Resource channelResource = resourceLoader.getResource(filePath);

            try {
                channelService.registerChannel(channelResource.getInputStream());
            } catch (IOException e) {
                logger.error(String.format("Cant read %s", filePath), e);
            }

        } else {
            logger.warn("Service is not set; could not register module as channel.");
        }
    }

    @Autowired(required = false)
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
