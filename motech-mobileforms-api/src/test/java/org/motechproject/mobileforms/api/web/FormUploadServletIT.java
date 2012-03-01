package org.motechproject.mobileforms.api.web;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.callbacks.FormProcessor;
import org.motechproject.mobileforms.api.callbacks.FormPublisher;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.validator.TestFormBean;
import org.motechproject.mobileforms.api.validator.TestFormValidator;
import org.motechproject.mobileforms.api.vo.Study;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jcraft.jzlib.ZInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationMobileFormsAPI.xml")
public class FormUploadServletIT {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockServletContext servletContext;
    @Mock
    FormPublisher formPublisher;
    @Mock
    private FormProcessor formProcessor;
    
    @Autowired 
    MobileFormsService mobileFormsService;

    @Before
    public void setUp() {
        initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();
    }

    @Test
    public void shouldProcessUploadedFormAndReturnValidationErrorsIfErrorsAreFound() throws Exception {
        FormUploadServlet formUploadServlet = new FormUploadServlet(mobileFormsService, null, formProcessor, formPublisher);
        formUploadServlet.setServletContext(servletContext);
        FormUploadServlet servlet = spy(formUploadServlet);

        List<FormBean> formBeans = new ArrayList<FormBean>();
        TestFormBean formBeanWithOutValidationErrors = new TestFormBean();
        formBeanWithOutValidationErrors.setValidator(TestFormValidator.class.getName());
        formBeanWithOutValidationErrors.setFirstName("Abc");
        TestFormBean formBeanWithValidationErrors = new TestFormBean();
        formBeanWithValidationErrors.setValidator(TestFormValidator.class.getName());
        formBeanWithValidationErrors.setFirstName("1Abc");
        formBeans.add(formBeanWithOutValidationErrors);
        formBeans.add(formBeanWithValidationErrors);
        Study study = new Study("study_name", formBeans);
        List<Study> studies = Arrays.asList(study);
        when(formProcessor.getStudies()).thenReturn(studies);

        EpihandyXformSerializer epihandyXformSerializer = spy(new EpihandyXformSerializer());
        doNothing().when(epihandyXformSerializer).deserializeStudiesWithEvents(any(DataInputStream.class), anyObject());

        doReturn(epihandyXformSerializer).when(servlet).serializer();

        TestFormValidator testFormValidator = new TestFormValidator();
        servletContext.setAttribute(TestFormValidator.class.getName(), testFormValidator);



        try {
            setupRequestWithActionAndOtherRequestParameters(request, "username", "password", FormDownloadServlet.ACTION_DOWNLOAD_STUDY_LIST);
            servlet.doPost(request, response);
            DataInputStream responseSentToMobile = readResponse(response);
            int expectedNoOfSuccessfullyUploadedForms = 1;
            int expectedNoOfFailedForms = 1;
            int expectedStudyIndex = 0;
            int expectedFormIndex = 1;
            assertThat(responseSentToMobile.readByte(), is(equalTo(FormDownloadServlet.RESPONSE_SUCCESS)));
            assertThat(responseSentToMobile.readInt(), is(equalTo(expectedNoOfSuccessfullyUploadedForms)));
            assertThat(responseSentToMobile.readInt(), is(equalTo(expectedNoOfFailedForms)));
            assertThat(responseSentToMobile.readByte(), is(equalTo((byte) expectedStudyIndex)));
            assertThat(responseSentToMobile.readShort(), is(equalTo((short) expectedFormIndex)));
            assertThat(responseSentToMobile.readUTF(), is(equalTo("Errors:firstName=wrong format")));

        } catch (Exception e) {
            assertFalse(true);
        }

        verify(formPublisher).publish(formBeanWithOutValidationErrors);

    }


    private DataInputStream readResponse(MockHttpServletResponse response) throws IOException {
        return new DataInputStream(new ZInputStream(new ByteArrayInputStream(response.getContentAsByteArray())));
    }

    private void setupRequestWithActionAndOtherRequestParameters(MockHttpServletRequest request, String userName, String password, byte actionCode) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF(userName);
        dataOutputStream.writeUTF(password);
        dataOutputStream.writeUTF("epihandyser");
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeByte(actionCode);
        request.setContent(byteArrayOutputStream.toByteArray());
    }
}
