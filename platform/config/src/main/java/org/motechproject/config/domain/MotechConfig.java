package org.motechproject.config.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Properties;

@TypeDiscriminator("doc.type === 'MotechConfig'")
public class MotechConfig extends MotechBaseDataObject {

    @JsonProperty
    private String module;
    @JsonProperty
    private Properties configs;

    private MotechConfig() {
    }

    public MotechConfig(String module, Properties configs) {
        this.module = module;
        this.configs = configs;
    }

    public String getModule() {
        return module;
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
