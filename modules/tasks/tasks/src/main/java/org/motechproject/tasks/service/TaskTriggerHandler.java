package org.motechproject.tasks.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskTriggerHandler {
    private static final String SERVICE_NAME = "taskTriggerHandler";
    private static final Integer TASK_POSSIBLE_ERRORS = 5;

    private static final Logger LOG = LoggerFactory.getLogger(TaskTriggerHandler.class);

    private TaskService taskService;
    private TaskStatusMessageService statusMessageService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;

    @Autowired
    public TaskTriggerHandler(TaskService taskService, TaskStatusMessageService statusMessageService, EventListenerRegistryService registryService, EventRelay eventRelay) {
        this.taskService = taskService;
        this.statusMessageService = statusMessageService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;

        registerHandler();
    }

    public void handler(final MotechEvent triggerEvent) {
        TaskEvent trigger;

        try {
            trigger = taskService.findTrigger(triggerEvent.getSubject());
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage(), e);
            return;
        }

        List<Task> tasks = taskService.findTasksForTrigger(trigger);

        for (Task t : tasks) {
            TaskEvent action;

            try {
                action = taskService.getActionEventFor(t);
            } catch (ActionNotFoundException e) {
                registerError(t, String.format("Not found action for: %s", t.getAction()));
                continue;
            }

            String subject = action.getSubject();

            if (StringUtils.isBlank(subject)) {
                registerError(t, String.format("Action '%s' does not have subject", action.getDisplayName()));
                continue;
            }

            List<EventParameter> actionEventParameters = action.getEventParameters();
            Map<String, Object> parameters = new HashMap<>(actionEventParameters.size());
            boolean send = true;

            for (EventParameter param : actionEventParameters) {
                String key = param.getEventKey();
                Object value = replaceAll(t.getActionInputFields().get(key), trigger.getEventParameters(), triggerEvent);

                if (StringUtils.isBlank(String.valueOf(value))) {
                    registerError(t, String.format("Not exist value for key: %s", key));
                    send = false;
                    break;
                }

                parameters.put(key, value);
            }

            if (send) {
                eventRelay.sendEventMessage(new MotechEvent(subject, parameters));
                statusMessageService.addSuccess(t);
            }
        }
    }

    private void registerHandler() {
        List<Task> tasks = taskService.getAllTasks();
        List<String> subjects = new ArrayList<>();

        for (Task t : tasks) {
            String subject = t.getTrigger().split(":")[1];
            subjects.add(subject);
        }

        if (!subjects.isEmpty()) {
            Method method = ReflectionUtils.findMethod(AopUtils.getTargetClass(this), "handler");
            EventListener proxy = new MotechListenerEventProxy(SERVICE_NAME, this, method);

            try {
                registryService.registerListener(proxy, subjects);
                LOG.info(String.format("Register TaskTriggerHandler for subjects: '%s'", subjects));
            } catch (Exception e) {
                LOG.error("Cant register TaskTriggerHandler", e);
            }
        }
    }

    private void registerError(final Task task, final String message) {
        statusMessageService.addError(task, message);
        LOG.error(message);

        if (statusMessageService.errorsFromLastRun(task).size() >= TASK_POSSIBLE_ERRORS) {
            task.setEnabled(false);
            taskService.save(task);
            statusMessageService.addWarning(task);
        }
    }

    private String replaceAll(final String template, final List<EventParameter> triggerEventParameters, final MotechEvent triggerEvent) {
        String replaced = template;

        for (EventParameter parameter : triggerEventParameters) {
            String key = parameter.getEventKey();
            String value = (String) triggerEvent.getParameters().get(key);

            replaced = replaced.replaceAll(String.format("{{%s}}", key), value);
        }

        return replaced;
    }

}
