package org.motechproject.tasks.service;

import java.util.Map;

public interface DataProviderLookupService {

    Object lookup(String clazz, Map<String, String> lookupFields);

    boolean supports(String clazz);

}
