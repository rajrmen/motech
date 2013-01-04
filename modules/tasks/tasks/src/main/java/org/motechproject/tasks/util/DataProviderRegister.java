package org.motechproject.tasks.util;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.tasks.service.DataProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

public class DataProviderRegister implements OsgiServiceRegistrationListener {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelRegister.class);
    private Resource dataProviderResource;

    public DataProviderRegister(Resource dataProviderResource) {
        this.dataProviderResource = dataProviderResource;
    }

    @Override
    public void registered(Object service, Map serviceProperties) throws IOException {
        if (service instanceof DataProviderService) {
            ((DataProviderService) service).registerProvider(dataProviderResource.getInputStream());
            LOG.info("Data provider registered");
        }
    }

    @Override
    public void unregistered(Object service, Map serviceProperties) {
        LOG.info("DataProviderService unregistered");
    }

}