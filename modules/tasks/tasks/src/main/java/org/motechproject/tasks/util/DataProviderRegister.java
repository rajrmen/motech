package org.motechproject.tasks.util;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.commons.api.DataProviderLookup;
import org.motechproject.tasks.service.DataProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

public class DataProviderRegister implements OsgiServiceLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelRegister.class);
    private DataProviderService dataProviderService;

    @Autowired
    public DataProviderRegister(DataProviderService dataProviderService) {
        this.dataProviderService = dataProviderService;
    }

    @Override
    public void bind(Object service, Map serviceProperties) throws IOException {
        if (service instanceof DataProviderLookup) {
            dataProviderService.registerProvider(((DataProviderLookup) service).toJSON());
            LOG.info("Data provider registered");
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        LOG.info("DataProviderService unregistered");
    }

}