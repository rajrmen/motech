package org.motechproject.security.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'MotechSecurityConfiguration'")
public class MotechSecurityConfiguration extends MotechBaseDataObject {

    public static final String DOC_TYPE = "MotechSecurityConfiguration";

    @JsonProperty
    private List<MotechURLSecurityRule> securityRules;

    public List<MotechURLSecurityRule> getSecurityRules() {
        return securityRules;
    }

    public void setSecurityRules(
            List<MotechURLSecurityRule> securityRules) {
        this.securityRules = securityRules;
    }
}
