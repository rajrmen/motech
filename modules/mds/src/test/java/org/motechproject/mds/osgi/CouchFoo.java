package org.motechproject.mds.osgi;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'CouchFoo'")
public class CouchFoo extends MotechBaseDataObject {

    @JsonProperty
    int couchInt;
    @JsonProperty
    String couchString;

    public CouchFoo() {
        super();
        this.setCouchInt(0);
        this.setCouchString(null);
    }

    public CouchFoo(int couchInt, String couchString) {
        super();
        this.couchInt = couchInt;
        this.couchString = couchString;
    }

    private String getCouchString() {
        return couchString;
    }

    private void setCouchString(String couchString) {
        this.couchString = couchString;
    }

    private int getCouchInt() {
        return couchInt;
    }

    private void setCouchInt(int couchInt) {
        this.couchInt = couchInt;
    }
}