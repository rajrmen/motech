package org.motechproject.server.web.dto;

import java.io.Serializable;

public class ModuleConfig implements Serializable {
    private String name;
    private String script;
    private String template;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
