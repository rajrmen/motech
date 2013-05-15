package org.motechproject.email;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.email.model.MailDetail;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Writer;

public class EmailSenderTest {

    @InjectMocks
    private EmailSender emailSender = new EmailSenderImpl();

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private VelocityEngine velocityEngine;

    @Mock
    private PlatformSettingsService settingsService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MotechSettings motechSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSendCriticalNotification() throws Exception {
        MailDetail statusMessage = new MailDetail("test msg", "test-module");
        Mockito.when(settingsService.getPlatformSettings()).thenReturn(motechSettings);
        Mockito.when(motechSettings.getServerUrl()).thenReturn("test.com");

        emailSender.sendCriticalNotificationEmail("test@address.com", statusMessage);

        ArgumentCaptor<MimeMessagePreparator> preparatorCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        Mockito.verify(javaMailSender).send(preparatorCaptor.capture());
        preparatorCaptor.getValue().prepare(mimeMessage);

        Mockito.verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress("test@address.com"));
        Mockito.verify(mimeMessage).setFrom(new InternetAddress("noreply@test.com"));
        Mockito.verify(mimeMessage).setSubject("Critical notification raised in Motech");

        ArgumentCaptor<VelocityContext> velocityContextCaptor = ArgumentCaptor.forClass(VelocityContext.class);
        Mockito.verify(velocityEngine).mergeTemplate(Matchers.eq("/mail/criticalNotification.vm"), velocityContextCaptor.capture(), Matchers.any(Writer.class));
        VelocityContext vc = velocityContextCaptor.getValue();

        Assert.assertEquals("test msg", vc.get("msg"));
        Assert.assertEquals("test-module", vc.get("module"));
        Assert.assertEquals("http://test.com/module/server/?moduleName=admin#/messages", vc.get("msgLink"));
    }
}
