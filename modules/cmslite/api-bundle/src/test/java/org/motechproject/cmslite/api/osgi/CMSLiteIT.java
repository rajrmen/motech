package org.motechproject.cmslite.api.osgi;


import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

public class CMSLiteIT extends BaseOsgiIT{
    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;
    }

    public void testCMSLiteService() {
        final ServiceReference serviceReference = bundleContext.getServiceReference(CMSLiteService.class.getName());
        assertNotNull(serviceReference);
        final CMSLiteService cmsLiteService = (CMSLiteService) bundleContext.getService(serviceReference);
        try {
        cmsLiteService.addContent(new StringContent("en", "title", "testContent"));
        } catch (Exception e) {
            fail();
        }


    }


}