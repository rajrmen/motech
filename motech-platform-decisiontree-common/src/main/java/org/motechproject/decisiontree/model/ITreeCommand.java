package org.motechproject.decisiontree.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Contract for a tree command.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface ITreeCommand {
    String[] execute(Object obj);
}
