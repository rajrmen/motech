package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.repository.AllTaskActivities;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

public class TaskActivityServiceImplTest {
    private static final String TASK_ID = "12345";

    @Mock
    AllTaskActivities allTaskActivities;

    TaskActivityService messageService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        messageService = new TaskActivityServiceImpl(allTaskActivities);
    }

    @Test
    public void test_errorsFromLastRun() {
        when(allTaskActivities.byTaskId(TASK_ID)).thenReturn(getTaskStatusMessages());

        Task t = new Task();
        t.setId(TASK_ID);

        List<TaskActivity> errors = messageService.errorsFromLastRun(t);

        assertNotNull(errors);
        assertEquals(4, errors.size());

        for (TaskActivity error : errors) {
            assertEquals(ERROR.getValue(), error.getMessage());
            assertEquals(TASK_ID, error.getTask());
            assertEquals(ERROR, error.getActivityType());
        }
    }

    private List<TaskActivity> getTaskStatusMessages() {
        List<TaskActivity> messages = new ArrayList<>();
        messages.add(createError());
        messages.add(createError());
        messages.add(createSuccess());
        messages.add(createError());
        messages.add(createError());
        messages.add(createWarning());
        messages.add(createSuccess());
        messages.add(createError());
        messages.add(createError());
        messages.add(createError());
        messages.add(createError());

        return messages;
    }

    private TaskActivity createError() {
        return new TaskActivity(ERROR.getValue(), TASK_ID, ERROR);
    }

    private TaskActivity createSuccess() {
        return new TaskActivity(SUCCESS.getValue(), TASK_ID, SUCCESS);
    }

    private TaskActivity createWarning() {
        return new TaskActivity(WARNING.getValue(), TASK_ID, WARNING);
    }
}
