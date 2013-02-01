package org.motechproject.tenant.service;

import org.springframework.web.context.request.RequestAttributes;

public interface TenantRepositoryService {

    public String getTenantId(RequestAttributes requestAttributes);
}
