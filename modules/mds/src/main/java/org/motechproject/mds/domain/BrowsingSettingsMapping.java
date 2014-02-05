package org.motechproject.mds.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mds.constants.Constants;
import org.motechproject.mds.dto.BrowsingSettingsDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = Constants.Util.TRUE)
public class BrowsingSettingsMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private EntityMapping entity;

    public BrowsingSettingsDto toDto() {
        BrowsingSettingsDto dto = new BrowsingSettingsDto();

        for (FieldMapping field : getFilterableFields()) {
            dto.addFilterableField(field.getId());
        }

        for (FieldMapping field : getDisplayedFields()) {
            dto.addDisplayedField(field.getId());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityMapping getEntity() {
        return entity;
    }

    public void setEntity(EntityMapping entity) {
        this.entity = entity;
    }

    public List<FieldMapping> getFilterableFields() {
        return getFields(new FilterableFieldPredicate());
    }

    public List<FieldMapping> getDisplayedFields() {
        return getFields(new DisplayedFieldPredicate());
    }

    private List<FieldMapping> getFields(Predicate predicate) {
        List<FieldMapping> fields = new ArrayList<>(getEntity().getFields());
        CollectionUtils.filter(fields, predicate);

        return fields;
    }

    private static class FilterableFieldPredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof FieldMapping && ((FieldMapping) object).isFilterable();
        }

    }

    private static class DisplayedFieldPredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof FieldMapping && ((FieldMapping) object).isDisplayed();
        }

    }
}
