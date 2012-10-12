package org.motechproject.testing.utils.osgi;

import org.apache.log4j.Logger;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.osgi.framework.ServiceReference;

public class CmsliteOsgiIT extends BaseOsgiIT {

    private Logger logger = Logger.getLogger(CmsliteOsgiIT.class);

    public void testCmsliteIsAvailable() {
        ServiceReference reference = bundleContext.getServiceReference(CMSLiteService.class.toString());
        System.out.println(reference);
    }
}
