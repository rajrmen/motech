package org.motechproject.mds.web.domain;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;

import java.util.List;

/**
 * Represents single field of entity instance
 */
public class FieldRecord {

    private static final String FORM_VALUES = "mds.form.label.values";

    private String name;
    private String displayName;
    private String tooltip;
    private Object value;
    private TypeDto type;
    private List<MetadataDto> metadata;
    private List<SettingDto> settings;
    private Long id;
    private boolean required;

    public FieldRecord() {
        this(null, null, null, null);
    }

    public FieldRecord(String name, String displayName, Object value, TypeDto type) {
        this.name = name;
        this.displayName = displayName;
        this.value = value;
        this.type = type;
    }

    public FieldRecord(FieldDto fieldDto) {
        this.name = fieldDto.getBasic().getName();
        this.displayName = fieldDto.getBasic().getDisplayName();
        this.type = fieldDto.getType();
        this.id = fieldDto.getId();
        this.metadata = fieldDto.getMetadata();
        this.settings = fieldDto.getSettings();
        this.tooltip = fieldDto.getBasic().getTooltip();
        this.required = fieldDto.getBasic().isRequired();
        setValue(fieldDto.getBasic().getDefaultValue());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        // we must include a user supplied value in the options for lists
        if (List.class.getName().equals(type.getTypeClass())) {
            extendOptionsIfNecessary();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeDto getType() {
        return type;
    }

    public void setType(TypeDto type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MetadataDto> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetadataDto> metadata) {
        this.metadata = metadata;
    }

    public List<SettingDto> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDto> settings) {
        this.settings = settings;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private void extendOptionsIfNecessary() {
        // don't add null or empty string
        if (value == null || value.equals("")) {
            return;
        }

        // find the correct option
        SettingDto listValuesOption = null;
        if (CollectionUtils.isNotEmpty(settings)) {
            for (SettingDto setting : settings) {
                if (FORM_VALUES.equals(setting.getName())) {
                    listValuesOption = setting;
                    break;
                }
            }
        }

        // add the value
        if (listValuesOption != null) {
            if (listValuesOption.getValue() instanceof List) {
                List listValues = (List) listValuesOption.getValue();
                if (!listValues.contains(value)) {
                    listValues.add(value);
                }
            }
        }
    }
}
