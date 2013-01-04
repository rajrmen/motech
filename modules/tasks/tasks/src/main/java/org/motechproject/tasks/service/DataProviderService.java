package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.DataProvider;

import java.io.InputStream;

public interface DataProviderService {

    void registerProvider(final InputStream stream);

    DataProvider getProvider(String name);

}
