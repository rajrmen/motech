package org.motechproject.mrs;

import org.motechproject.commons.api.AbstractDataProviderLookup;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MRSDataProviderLookup extends AbstractDataProviderLookup {

    @Autowired
    public MRSDataProviderLookup(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");

        if (resource != null) {
            setBody(resource);
        }
    }

    @Override
    public Object lookup(String clazz, Map<String, String> lookupFields) {
        return null;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        return Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);
    }
}
