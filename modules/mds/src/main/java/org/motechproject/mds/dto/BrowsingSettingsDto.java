package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>BrowsingSettingsDto</code> contains informations about filed browsing settings
 */
public class BrowsingSettingsDto {

    private List<Long> filterableFields = new ArrayList<>();
    private List<Long> displayedFields = new ArrayList<>();

    public void addFilterableField(Number id) {
        this.filterableFields.add(id.longValue());
    }

    public void removeFilterableField(Number id) {
        this.filterableFields.remove(id.longValue());
    }

    public List<Long> getFilterableFields() {
        return filterableFields;
    }

    public void setFilterableFields(List<Long> filterableFields) {
        this.filterableFields = null != filterableFields ? filterableFields : new ArrayList<Long>();
    }

    public void addDisplayedField(Number id) {
        this.displayedFields.add(id.longValue());
    }

    public List<Long> getDisplayedFields() {
        return displayedFields;
    }

    public void setDisplayedFields(List<Long> displayedFields) {
        this.displayedFields = null != displayedFields ? displayedFields : new ArrayList<Long>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
