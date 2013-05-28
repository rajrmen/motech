package org.motechproject.email.osgi;

import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.List;

import static java.util.Arrays.asList;


public class EmailBundleIT extends BaseOsgiIT{

    public void testEmailService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(EmailSenderService.class.getName());
        assertNotNull(serviceReference);
        EmailSenderService emailSenderService = (EmailSenderService) bundleContext.getService(serviceReference);
        assertNotNull(emailSenderService);
        emailSenderService.send(new Mail("from@from.com","to@to.com","test","test"));
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.springframework.mail.javamail"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testblueprint.xml"};
    }
}
