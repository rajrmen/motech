package org.motechproject.commons.api;

import java.util.Map;

public interface DataProviderLookup {

    String toJSON();

    Object lookup(String clazz, Map<String, String> lookupFields);

    boolean supports(String clazz);

}
