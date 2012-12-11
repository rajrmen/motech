package org.motechproject.ivr.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.service.CallDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class CallDetailRecordsController {

    @Autowired
    private CallDetailService callDetailService;

    @RequestMapping(value = "/call_detail_records", method = RequestMethod.GET)
    @ResponseBody public String findAllCallDetailRecords() throws IOException {
        List<CallDetailRecord> detailRecords = callDetailService.getAll();
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(detailRecords);
    }

}
