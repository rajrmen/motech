package org.motechproject.server.service;

public class TenantServiceImpl implements TenantService {

    private String tenantId;

    public TenantServiceImpl(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }
}
