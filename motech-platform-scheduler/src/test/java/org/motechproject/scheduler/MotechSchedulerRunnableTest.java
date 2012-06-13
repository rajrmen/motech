/*
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2012 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.scheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.motechproject.scheduler.MotechSchedulerConstants.CONNECTION_CLOSE_INPUT_PARAM;
import static org.motechproject.scheduler.MotechSchedulerConstants.EVENT_MESSAGE_CODE;
import static org.motechproject.scheduler.MotechSchedulerConstants.EVENT_MESSAGE_INPUT_PARAM;
import static org.motechproject.scheduler.MotechSchedulerConstants.PARAMETERS;
import static org.motechproject.scheduler.MotechSchedulerConstants.SUBJECT;
import static org.motechproject.scheduler.MotechSchedulerConstants.UNKNOWN_CODE;
import static org.motechproject.scheduler.MotechSchedulerService.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationPlatformScheduler.xml"})
public class MotechSchedulerRunnableTest {
    private static final String TEST_EVENT_NAME = "testEvent";
    private static final String TEST_SUBJECT = "test";
    private static final String TEST_CRON_EXPRESSION = "0/5 * * * * ?";

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    private Map<String, Object> eventParams = new HashMap<String, Object>() {
        {
            put(JOB_ID_KEY, TEST_EVENT_NAME);
        }
    };

    @Autowired
    private MotechScheduler motechScheduler;

    @Before
    public void setUp() throws Exception {
        new Thread(motechScheduler).start();
        client = new Socket("localhost", 5000);
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        assertEquals("Motech Scheduler started...", in.readLine());
    }

    @After
    public void setDown() throws Exception {
        out.println(CONNECTION_CLOSE_INPUT_PARAM);

        assertEquals("Motech Scheduler stopped...", in.readLine());

        in.close();
        out.close();
        client.close();
    }

    @Test
    public void shouldReturnUnknownCode() throws Exception {
        out.println("-motech");
        assertEquals(UNKNOWN_CODE, in.readLine());
    }

    @Test
    public void shouldSendMotechEventMessage() throws Exception {
        String messageToSend = motechScheduler.createEventMessageArgs(TEST_SUBJECT, eventParams);

        assertEquals(String.format("%s %s %s %s {\"%s\":\"%s\"}", EVENT_MESSAGE_INPUT_PARAM, SUBJECT, TEST_SUBJECT,
                PARAMETERS, JOB_ID_KEY, TEST_EVENT_NAME), messageToSend);

        out.println(messageToSend);
        assertEquals(EVENT_MESSAGE_CODE, in.readLine());
    }

}
