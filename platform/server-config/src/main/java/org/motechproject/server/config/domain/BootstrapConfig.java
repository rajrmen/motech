package org.motechproject.server.config.domain;

public class BootstrapConfig {

    public static final String DEFAULT_TENANT_ID = "DEFAULT";
    private String dbhost;
    private String tenantId = DEFAULT_TENANT_ID;

    public String getDBHost() {
        return dbhost;
    }

    public void setDBHost(String dbhost) {
        this.dbhost = dbhost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BootstrapConfig that = (BootstrapConfig) o;

        if (!dbhost.equals(that.dbhost)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return dbhost.hashCode();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
