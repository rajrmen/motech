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
    private List<MotechPermission> permissions;

    public MotechRoleCouchdbImpl() {
        super();
        this.setType(DOC_TYPE);
    }

    public MotechRoleCouchdbImpl(String roleName, List<MotechPermission> permissions) {
        super();
        this.roleName = roleName;
        this.permissions = permissions;
        this.setType(DOC_TYPE);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<MotechPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<MotechPermission> permissions) {
        this.permissions = permissions;
    }
}
