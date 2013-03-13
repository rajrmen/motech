package org.motechproject.cmslite.api.web;

import org.motechproject.cmslite.api.model.Content;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ContentDto implements Serializable {
    private static final long serialVersionUID = 6728665456455509425L;

    private final Set<String> languages = new HashSet<>();
    private final String name;
    private final String type;

    public ContentDto(Content content) {
        this.name = content.getName();
        this.type = content.getType();
        this.languages.add(content.getLanguage());
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
}
