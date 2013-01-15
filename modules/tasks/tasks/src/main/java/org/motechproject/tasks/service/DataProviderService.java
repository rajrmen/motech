package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.DataProvider;

import java.io.InputStream;
import java.util.List;

public interface DataProviderService {

    void registerProvider(String json);

    void registerProvider(final InputStream stream);

    DataProvider getProvider(String name);

    List<DataProvider> getProviders();

}
