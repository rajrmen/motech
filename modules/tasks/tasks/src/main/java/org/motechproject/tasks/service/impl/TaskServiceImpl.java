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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("taskService")
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
    public void save(final Task task) {
        try {
            allTasks.addOrUpdate(task);
            logger.info(String.format("Saved task: %s", task.getId()));
        } catch (BusinessIdNotUniqueException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public TaskEvent getActionEventFor(final Task task) {
        String[] actionArray = task.getAction().split(":");
        Channel channel = channelService.getChannel(actionArray[0]);
        TaskEvent event = null;

        for (TaskEvent action : channel.getActionTaskEvents()) {
            if (action.getSubject().equalsIgnoreCase(actionArray[1])) {
                event = action;
                break;
            }
        }

        return event;
    }

    @Override
    public List<Task> getAllTasks() {
        return allTasks.getAll();
    }

    @Override
    public List<Task> findTasksForTrigger(final String subject) {
        TaskEvent trigger = findTrigger(subject);

        List<Task> tasks = allTasks.getAll();
        List<Task> result = new ArrayList<>(tasks.size());

        if (trigger != null) {
            for (Task t : tasks) {
                String triggerKey = t.getTrigger().split(":")[0];

                if (triggerKey.equalsIgnoreCase(trigger.getSubject())) {
                    result.add(t);
                }
            }
        }

        return result;
    }

    private TaskEvent findTrigger(String subject) {
        List<Channel> channels = channelService.getAllChannels();
        TaskEvent trigger = null;

        for (Channel c : channels) {
            for (TaskEvent t : c.getTriggerTaskEvents()) {
                if (t.getSubject().equalsIgnoreCase(subject)) {
                    trigger = t;
                    break;
                }
            }

            if (trigger != null) {
                break;
            }
        }

        return trigger;
    }

}
