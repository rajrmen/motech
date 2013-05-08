package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSUser;

@TypeDiscriminator("doc.type === 'CouchMrsUser'")
public class CouchMrsUser extends MotechBaseDataObject implements MRSUser {

    private String userId;
    private String systemId;
    private String securityRole;
    private String userName;
    private CouchPerson person;

    private CouchMrsUser() {
    }

    public CouchMrsUser(String userId, String systemId, String securityRole, String userName, CouchPerson person) {
        this.userId = userId;
        this.systemId = systemId;
        this.securityRole = securityRole;
        this.userName = userName;
        this.person = person;
    }

    @Override
    @JsonProperty
    public String getUserId() {
        return userId;
    }

    @Override
    @JsonProperty
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    @JsonProperty
    public String getSystemId() {
        return systemId;
    }

    @Override
    @JsonProperty
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    @JsonProperty
    public String getSecurityRole() {
        return securityRole;
    }

    @Override
    @JsonProperty
    public void setSecurityRole(String securityRole) {
        this.securityRole = securityRole;
    }

    @Override
    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @Override
    @JsonProperty
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty
    public String getPersonId() {
        return person.getPersonId();
    }

    @JsonProperty
    public void setPersonId(String personId) {
        this.person.setPersonId(personId);
    }

    @Override
    @JsonIgnore
    public MRSPerson getPerson() {
        return person;
    }

    @Override
    @JsonIgnore
    public void setPerson(MRSPerson person) {
        this.person = (CouchPerson) person;
    }
}
