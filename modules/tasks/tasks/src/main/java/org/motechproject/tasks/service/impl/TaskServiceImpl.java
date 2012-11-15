package org.motechproject.tasks.service.impl;

import org.motechproject.dao.BusinessIdNotUniqueException;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskServiceImpl implements TaskService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AllTasks allTasks;
    private ChannelService channelService;

    @Autowired
    public TaskServiceImpl(final AllTasks allTasks, final ChannelService channelService) {
        this.allTasks = allTasks;
        this.channelService = channelService;
    }

    @Override
    public void add(final Task task) {
        try {
            allTasks.addOrUpdate(task);
            logger.info(String.format("Saved task: %s", task.getId()));
        } catch (BusinessIdNotUniqueException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public TaskEvent getTriggerEventFor(final Task task) {
        String[] triggerArray = task.getTrigger().split(":");
        Channel channel = channelService.getChannel(triggerArray[0]);
        TaskEvent event = null;

        for (TaskEvent trigger : channel.getTriggerTaskEvents()) {
            String[] eventKey = trigger.getEventKey().split(".");

            if (eventKey[1].equalsIgnoreCase(triggerArray[1])) {
                event = trigger;
                break;
            }
        }

        return event;
    }

    @Override
    public TaskEvent getActionEventFor(final Task task) {
        String[] triggerArray = task.getTrigger().split(":");
        Channel channel = channelService.getChannel(triggerArray[0]);
        TaskEvent event = null;

        for (TaskEvent action : channel.getActionTaskEvents()) {
            String[] eventKey = action.getEventKey().split(".");

            if (eventKey[1].equalsIgnoreCase(triggerArray[1])) {
                event = action;
                break;
            }
        }

        return event;
    }

}
