package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.DataProvider;
import org.motechproject.tasks.service.DataProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DataProviderController {
    private DataProviderService dataProviderService;

    @Autowired
    public DataProviderController(DataProviderService dataProviderService) {
        this.dataProviderService = dataProviderService;
    }

    @RequestMapping(value = "datasource", method = RequestMethod.GET)
    @ResponseBody
    public List<DataProvider> getAllDataProviders() {
        return dataProviderService.getProviders();
    }
}
