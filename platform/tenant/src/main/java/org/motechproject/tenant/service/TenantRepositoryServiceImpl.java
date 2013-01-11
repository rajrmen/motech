package org.motechproject.tenant.service;

import org.motechproject.tenant.ex.NoTenantException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service("tenantRepositoryService")
public class TenantRepositoryServiceImpl implements TenantRepositoryService {

    @Override
    public String getTenantId(RequestAttributes requestAttributes) {
        if (requestAttributes == null) {
            throw new NoTenantException("No request data available");
        }

        HttpServletRequest httpServletRequest = toHttpServletRequest(requestAttributes);
        RequestAttributes request =  RequestContextHolder.currentRequestAttributes();

        String tenantId = getId(httpServletRequest);

        if (tenantId == null) {
            throw new NoTenantException("No tenant found for " + httpServletRequest.getLocalName());
        }

        return tenantId;
    }

    private HttpServletRequest toHttpServletRequest(RequestAttributes requestAttributes) {
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    private String getId(HttpServletRequest httpServletRequest) {
        String tenantId = null;

        switch (httpServletRequest.getLocalName()) {
            case "localhost":
            case "127.0.0.1":
                tenantId = "0001";
                break;
            case "seth":
                tenantId = "0002";
                break;
        }

        return tenantId;
    }
}
