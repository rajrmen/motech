package org.motechproject.tasks.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataProviderObject {
    private String displayName;
    private String type;
    private List<String> fields;
    private String service;
    private String lookupMethod;

    public DataProviderObject() {
        this(null, null, new ArrayList<String>(), null, null);
    }

    public DataProviderObject(String displayName, String type, List<String> fields, String service, String lookupMethod) {
        this.displayName = displayName;
        this.type = type;
        this.fields = fields;
        this.service = service;
        this.lookupMethod = lookupMethod;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLookupMethod() {
        return lookupMethod;
    }

    public void setLookupMethod(String lookupMethod) {
        this.lookupMethod = lookupMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataProviderObject that = (DataProviderObject) o;

        return Objects.equals(displayName, that.displayName) && Objects.equals(fields, that.fields) &&
                Objects.equals(lookupMethod, that.lookupMethod) && Objects.equals(service, that.service) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        int result = displayName != null ? displayName.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (lookupMethod != null ? lookupMethod.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("DataProviderObject{displayName='%s', type='%s', fields=%s, service='%s', lookupMethod='%s'}",
                displayName, type, fields, service, lookupMethod);
    }
}
