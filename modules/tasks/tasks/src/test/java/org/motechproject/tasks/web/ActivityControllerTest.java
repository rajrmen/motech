package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.TaskStatusMessage;
import org.motechproject.tasks.service.TaskStatusMessageService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.Level.ERROR;
import static org.motechproject.tasks.domain.Level.SUCCESS;
import static org.motechproject.tasks.domain.Level.WARNING;

public class ActivityControllerTest {
    private static final String TASK_ID = "12345";

    @Mock
    TaskStatusMessageService messageService;

    ActivityController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ActivityController(messageService);
    }

    @Test
    public void shouldGetAllActivities() {
        List<TaskStatusMessage> expected = new ArrayList<>();
        expected.add(new TaskStatusMessage(SUCCESS.getValue(), TASK_ID, SUCCESS));
        expected.add(new TaskStatusMessage(WARNING.getValue(), TASK_ID, WARNING));
        expected.add(new TaskStatusMessage(ERROR.getValue(), TASK_ID, ERROR));

        when(messageService.getAllMessages()).thenReturn(expected);

        List<TaskStatusMessage> actual = controller.getAllActivities();

        verify(messageService).getAllMessages();

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

}
