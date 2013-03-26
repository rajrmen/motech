package org.motechproject.cmslite.api.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.cmslite.api.model.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public final class ResourceFilter {

    private ResourceFilter() {
    }

    public static List<ResourceDto> filter(final String name, final boolean string, final boolean stream,
                                           final String languages, final List<Content> contents) {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        final List<String> languagesList = new ArrayList<>();

        if (isNotBlank(languages)) {
            languagesList.addAll(Arrays.asList(languages.split(",")));
        }

        for (final Content content : contents) {
            if (isCorrect(content, name, string, stream, languagesList)) {
                ResourceDto dto = (ResourceDto) CollectionUtils.find(resourceDtos, new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return object instanceof ResourceDto &&
                                equalsContent((ResourceDto) object, content.getName(), getContentType(content));
                    }
                });

                if (dto == null) {
                    resourceDtos.add(new ResourceDto(content));
                } else {
                    dto.addLanguage(content.getLanguage());
                }
            }
        }

        return resourceDtos;
    }

    private static boolean isCorrect(Content content, String name, boolean string, boolean stream, List<String> languages) {
        final String contentType = getContentType(content);

        boolean equalLanguage = languages.isEmpty() || languages.contains(content.getLanguage());
        boolean equalName = isBlank(name) || startsWithIgnoreCase(content.getName(), name);
        boolean equalString = string && equalsIgnoreCase(contentType, "string");
        boolean equalStream = stream && equalsIgnoreCase(contentType, "stream");

        return equalLanguage && equalName && (equalString || equalStream);
    }

    private static String getContentType(Content content) {
        String contentType;

        if (startsWithIgnoreCase(content.getType(), "string")) {
            contentType = "string";
        } else if (startsWithIgnoreCase(content.getType(), "stream")) {
            contentType = "stream";
        } else {
            contentType = "";
        }

        return contentType;
    }

    private static boolean equalsContent(ResourceDto dto, String contentName, String contentType) {
        return equalsIgnoreCase(dto.getName(), contentName) && equalsIgnoreCase(dto.getType(), contentType);
    }
}
