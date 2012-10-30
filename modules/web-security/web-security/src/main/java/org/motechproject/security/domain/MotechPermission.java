package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 23.10.12
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
public interface MotechPermission {

    String getPermissionName();

    String getBundleName();
}
