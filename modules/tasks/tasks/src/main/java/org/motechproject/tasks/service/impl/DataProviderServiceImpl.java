package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.DataProvider;
import org.motechproject.tasks.repository.AllDataProviders;
import org.motechproject.tasks.service.DataProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

@Service("dataProviderService")
public class DataProviderServiceImpl implements DataProviderService {
    private AllDataProviders allDataProviders;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public DataProviderServiceImpl(AllDataProviders allDataProviders) {
        this(allDataProviders, new MotechJsonReader());
    }

    public DataProviderServiceImpl(AllDataProviders allDataProviders, MotechJsonReader motechJsonReader) {
        this.allDataProviders = allDataProviders;
        this.motechJsonReader = motechJsonReader;
    }

    @Override
    public void registerProvider(final String body) {
        byte[] bytes = body.getBytes();
        InputStream stream = new ByteArrayInputStream(bytes);

        registerProvider(stream);
    }

    @Override
    public void registerProvider(final InputStream stream) {
        Type type = new TypeToken<DataProvider>() { }.getType();
        DataProvider provider = (DataProvider) motechJsonReader.readFromStream(stream, type);

        allDataProviders.addOrUpdate(provider);
    }

    @Override
    public DataProvider getProvider(String name) {
        return allDataProviders.byName(name);
    }

    @Override
    public List<DataProvider> getProviders() {
        return allDataProviders.getAll();
    }

}
