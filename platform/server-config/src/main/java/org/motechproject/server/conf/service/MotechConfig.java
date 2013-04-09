package org.motechproject.server.conf.service;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Map;

@TypeDiscriminator("doc.type === 'MotechConfig'")
public class MotechConfig extends MotechBaseDataObject {

    @JsonProperty
    private String module;
    @JsonProperty
    private Map<String, String> configs;

    public MotechConfig() {
    }

    public MotechConfig(String module, Map<String, String> configs) {
        this.module = module;
        this.configs = configs;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("MotechConfig");
        sb.append("{module='").append(module).append('\'');
        sb.append(", configs=").append(configs);
        sb.append('}');
        return sb.toString();
    }
}
