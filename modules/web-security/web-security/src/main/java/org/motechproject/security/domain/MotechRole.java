package org.motechproject.security.domain;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lukasz
 * Date: 23.10.12
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public interface MotechRole {

    String getRoleName();

    List<String> getPermissionNames();
}
