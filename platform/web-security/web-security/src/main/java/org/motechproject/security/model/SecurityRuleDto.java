package org.motechproject.security.model;

import java.util.List;
import java.util.Set;

import org.motechproject.security.domain.MotechURLSecurityRule;

public class SecurityRuleDto implements MotechURLSecurityRule {

    private String pattern;
    private List<String> supportedSchemes;
    private String protocol;
    private List<String> permissionAccess;
    private List<String> userAccess;
    private int priority;
    private boolean rest;
    private String origin;
    private String version;
    private Set<String> methodsRequired;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List<String> getSupportedSchemes() {
        return supportedSchemes;
    }

    public void setSupportedSchemes(List<String> supportedSchemes) {
        this.supportedSchemes = supportedSchemes;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<String> getMethodsRequired() {
        return methodsRequired;
    }

    public void setMethodsRequired(Set<String> methodsRequired) {
        this.methodsRequired = methodsRequired;
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
}
