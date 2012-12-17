package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.TaskStatusMessage;
import org.motechproject.tasks.service.TaskStatusMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ActivityController {
    private TaskStatusMessageService messageService;

    @Autowired
    public ActivityController(final TaskStatusMessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskStatusMessage> getAllActivities() {
        return messageService.getAllMessages();
    }
}
