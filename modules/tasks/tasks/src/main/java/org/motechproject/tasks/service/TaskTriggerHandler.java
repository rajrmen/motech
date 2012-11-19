package org.motechproject.tasks.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskTriggerHandler {
    private static final String SERVICE_NAME = "taskTriggerHandler";

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;

    @Autowired
    public TaskTriggerHandler(final TaskService taskService, final EventListenerRegistryService registryService, final EventRelay eventRelay) {
        this.taskService = taskService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;

        try {
            registerHandler();
        } catch (NoSuchMethodException e) {
            LOG.error("Cant register TaskTriggerHandler", e);
        }
    }

    public void handler(final MotechEvent trigger) {
        List<Task> tasks = taskService.findTasksForTrigger(trigger.getSubject());

        for (Task t : tasks) {
            TaskEvent action = taskService.getActionEventFor(t);

            String subject = action.getSubject();

            List<EventParameter> actionEventParameters = action.getEventParameters();
            Map<String, Object> parameters = new HashMap<>(actionEventParameters.size());

            for (EventParameter param : actionEventParameters) {
                String key = param.getEventKey();
                Object value = t.getActionInputFields().get(param.getDisplayName());

                parameters.put(key, value);
            }

            eventRelay.sendEventMessage(new MotechEvent(subject, parameters));
        }
    }

    private void registerHandler() throws NoSuchMethodException {
        List<Task> tasks = taskService.getAllTasks();
        List<String> subjects = new ArrayList<>();

        for (Task t : tasks) {
            subjects.add(t.getTrigger().split(":")[1]);
        }

        if (!subjects.isEmpty()) {
            Method method = ReflectionUtils.findMethod(AopUtils.getTargetClass(this), "handler");
            EventListener proxy = new MotechListenerEventProxy(SERVICE_NAME, this, method);

            registryService.registerListener(proxy, subjects);
        }
    }

}
