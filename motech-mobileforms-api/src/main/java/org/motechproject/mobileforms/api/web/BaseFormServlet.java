package org.motechproject.mobileforms.api.web;

import java.io.DataInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.callbacks.FormProcessor;
import org.motechproject.mobileforms.api.callbacks.FormPublisher;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormOutput;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.service.UsersService;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

public abstract class BaseFormServlet implements ServletContextAware {

    public static final byte RESPONSE_ERROR = 0;
    public static final byte RESPONSE_SUCCESS = 1;

    public static final String FAILED_TO_SERIALIZE_DATA = "failed to serialize data";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private final Logger log = LoggerFactory.getLogger(FormProcessor.class);
    private ServletContext servletContext;

    protected UsersService usersService;
    protected FormProcessor formProcessor;
    protected FormPublisher formPublisher;
    protected MobileFormsService mobileFormsService;

    @Autowired
    public BaseFormServlet(MobileFormsService mobileFormsService, UsersService usersService, FormProcessor formProcessor, FormPublisher formPublisher) {
        this.mobileFormsService = mobileFormsService;
        this.usersService = usersService;
        this.formProcessor = formProcessor;
        this.formPublisher = formPublisher;
    }

    protected EpihandyXformSerializer serializer() {
        return new EpihandyXformSerializer();
    }

    protected void readParameters(DataInputStream dataInput) throws IOException {
        String name = dataInput.readUTF();
        String password = dataInput.readUTF();
        String serializer = dataInput.readUTF();
        String locale = dataInput.readUTF();
    }

    protected FormValidator getValidatorFor(FormBean formBean) throws ClassNotFoundException {
        return (FormValidator) servletContext.getAttribute(formBean.getValidator());
    }
    
    public void setServletContext(ServletContext servletContext) {
    	this.servletContext = servletContext;
    }

    protected byte readActionByte(DataInputStream dataInput) throws IOException {
        return dataInput.readByte();
    }

    protected FormOutput getFormOutput() {
        return new FormOutput();
    }
}
