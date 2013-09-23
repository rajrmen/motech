package org.motechproject.security.domain;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'MotechSecurityRule'")
public class MotechURLSecurityRuleCouchdbImpl extends MotechBaseDataObject implements MotechURLSecurityRule {

    public static final String DOC_TYPE = "MotechSecurityRule";

    @JsonProperty
    private String pattern;

    @JsonProperty
    private List<String> supportedSchemes;

    @JsonProperty
    private String protocol;

    @JsonProperty
    private List<String> permissionAccess;

    @JsonProperty
    private List<String> userAccess;

    @JsonProperty
    private int priority;

    @JsonProperty
    private boolean rest;

    @JsonProperty
    private String origin;

    @JsonProperty
    private String version;

    public MotechURLSecurityRuleCouchdbImpl() {
        super();
        this.setType(DOC_TYPE);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getSupportedSchemes() {
        return supportedSchemes;
    }

    public void setSupportedSchemes(List<String> supportedSchemes) {
        this.supportedSchemes = supportedSchemes;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<String> getPermissionAccess() {
        return permissionAccess;
    }

    public void setPermissionAccess(List<String> permissionAccess) {
        this.permissionAccess = permissionAccess;
    }

    public List<String> getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(List<String> userAccess) {
        this.userAccess = userAccess;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
