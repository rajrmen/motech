package org.motechproject.cmslite.api.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.cmslite.api.model.Content;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public class ResourceDto implements Serializable {
    private static final long serialVersionUID = 6728665456455509425L;

    private final Set<String> languages = new HashSet<>();
    private final String name;
    private final String type;

    public ResourceDto(Content content) {
        this.name = content.getName();
        this.languages.add(content.getLanguage());

        if (startsWithIgnoreCase(content.getType(), "string")) {
            type = "string";
        } else if (startsWithIgnoreCase(content.getType(), "stream")) {
            type = "stream";
        } else {
            type = null;
        }
    }

    public void addLanguage(String language) {
        languages.add(language);
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("ResourceDto{languages=%s, name='%s', type='%s'}", languages, name, type);
    }
}
