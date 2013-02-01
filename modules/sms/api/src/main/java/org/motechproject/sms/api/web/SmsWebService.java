package org.motechproject.sms.api.web;

import org.motechproject.sms.api.exceptions.SendSmsException;
import org.motechproject.sms.api.service.SendSmsRequest;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("messages")
public class SmsWebService implements SmsService {

    @Autowired
    @Qualifier("smsServiceImpl")
    private SmsService smsService;

    public SmsWebService() {
    }

    public SmsWebService(SmsService smsService) {
        this();
        this.smsService = smsService;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void sendSMS(@RequestBody SendSmsRequest request) {
        smsService.sendSMS(request);
    }

    @ExceptionHandler(SendSmsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String exceptionHandler(SendSmsException exception) {
        return exception.getCause().getMessage();
    }
}
