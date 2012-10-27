package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 23.10.12
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
@TypeDiscriminator("doc.type == 'MotechRole'")
public class MotechRoleCouchdbImpl extends MotechBaseDataObject implements MotechRole {

    public static final String DOC_TYPE = "MotechRole";

    @JsonProperty
    private String roleName;

    @JsonProperty
    private List<String> permissionNames;

    public MotechRoleCouchdbImpl() {
        super();
        this.setType(DOC_TYPE);
    }

    public MotechRoleCouchdbImpl(String roleName, List<String> permissionNames) {
        super();
        this.roleName = roleName;
        this.permissionNames = permissionNames;
        this.setType(DOC_TYPE);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissionNames() {
        return permissionNames;
    }

    public void setPermissionNames(List<String> permissionNames) {
        this.permissionNames = permissionNames;
    }
}
