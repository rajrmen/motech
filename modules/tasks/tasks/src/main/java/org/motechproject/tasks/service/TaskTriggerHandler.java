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
    private TaskErrorService errorService;
    private EventListenerRegistryService registryService;
    private EventRelay eventRelay;

    @Autowired
    public TaskTriggerHandler(TaskService taskService, TaskErrorService errorService, EventListenerRegistryService registryService, EventRelay eventRelay) {
        this.taskService = taskService;
        this.errorService = errorService;
        this.registryService = registryService;
        this.eventRelay = eventRelay;

        registerHandler();
    }

    public void handler(final MotechEvent trigger) {
        List<Task> tasks;

        try {
            tasks = taskService.findTasksForTrigger(trigger.getSubject());
        } catch (TriggerNotFoundException e) {
            LOG.error(e.getMessage(), e);
            return;
        }

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
                Object value = t.getActionInputFields().get(param.getEventKey());

                if (StringUtils.isBlank(String.valueOf(value))) {
                    registerError(t, String.format("Not exist value for key: %s", key));
                    send = false;
                    break;
                }

                parameters.put(key, value);
            }

            if (send) {
                eventRelay.sendEventMessage(new MotechEvent(subject, parameters));
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
        errorService.addTaskError(task, message);
        LOG.error(message);

        if (errorService.getTaskErrors(task).size() >= TASK_POSSIBLE_ERRORS) {
            task.setEnabled(false);
            taskService.save(task);
            LOG.info(String.format("Task %s disabled.", task));
        }
    }

}
