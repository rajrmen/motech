package org.motechproject.server.decisiontree.service;

public class TreeNotFoundException extends RuntimeException {
    public TreeNotFoundException(String treeName) {
        super("Tree not found :" + treeName);
    }
}
