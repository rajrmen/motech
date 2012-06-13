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

public class MotechSchedulerConstants {

    public final static String PREFFIX_CODE = "CODE: ";
    public final static String PREFFIX_ERROR = "ERROR: ";

    public final static String SCHEDULE_TEST_INPUT_PARAM = "-st";
    public final static String UNSCHEDULE_TEST_INPUT_PARAM = "-ust";

    public final static String EVENT_MESSAGE_INPUT_PARAM = "-e";
    public final static String CRON_SCHEDULABLE_JOB_INPUT_PARAM = "-csj";
    public final static String REPEATING_SCHEDULABLE_JOB_INPUT_PARAM = "-rsj";
    public final static String RUN_ONCE_SCHEDULABLE_JOB_INPUT_PARAM = "-rosj";

    public final static String CONNECTION_CLOSE_INPUT_PARAM = "-c";

    public final static String SUBJECT = "-s";
    public final static String PARAMETERS = "-p";
    public final static String CRON_EXPRESSION = "-ce";
    public final static String START_DATE = "-sd";
    public final static String END_DATE = "-ed";
    public final static String REPEAT_COUNT = "-rc";
    public final static String REPEAT_INTERVAL = "-ri";

    public final static String UNKNOWN_CODE = PREFFIX_CODE + "UNKNOWN";
    public final static String EVENT_MESSAGE_CODE = PREFFIX_CODE + "EVENT";
    public final static String CRON_SCHEDULABLE_JOB_CODE = PREFFIX_CODE + "CRON";
    public final static String REPEATING_SCHEDULABLE_JOB_CODE = PREFFIX_CODE + "REPEATING";
    public final static String RUN_ONCE_SCHEDULABLE_JOB_CODE = PREFFIX_CODE + "RUN ONE";

}
