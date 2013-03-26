package org.motechproject.cmslite.api.web;

import org.apache.commons.collections.Predicate;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public class ResourceFilter implements Predicate {
    private final String name;
    private final boolean string;
    private final boolean stream;

    public ResourceFilter(String name, boolean string, boolean stream) {
        this.name = name;
        this.string = string;
        this.stream = stream;
    }

    @Override
    public boolean evaluate(Object object) {
        boolean result = object instanceof ResourceDto;

        if (result) {
            ResourceDto dto = (ResourceDto) object;

            if (isNotBlank(name)) {
                result = startsWithIgnoreCase(dto.getName(), name);
            }

            if (!string) {
                result = result && !equalsIgnoreCase(dto.getType(), "string");
            }

            if (!stream) {
                result = result && !equalsIgnoreCase(dto.getType(), "stream");
            }
        }

        return result;
    }
}
