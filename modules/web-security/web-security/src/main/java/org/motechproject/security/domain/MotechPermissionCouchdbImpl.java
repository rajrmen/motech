package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 23.10.12
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
@TypeDiscriminator("doc.type == 'MotechPermission'")
public class MotechPermissionCouchdbImpl extends MotechBaseDataObject implements MotechPermission {

    public static final String DOC_TYPE = "MotechPermission";

    @JsonProperty
    private String permissionName;

    public MotechPermissionCouchdbImpl() {
        super();
        this.setType(DOC_TYPE);
    }

    public MotechPermissionCouchdbImpl(String permissionName) {
        super();
        this.permissionName = permissionName;
        this.setType(DOC_TYPE);
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
}
