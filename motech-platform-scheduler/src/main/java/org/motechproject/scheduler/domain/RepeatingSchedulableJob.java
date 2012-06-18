/*
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
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
package org.motechproject.scheduler.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Schedulable Job - a data carrier class for a scheduled job that can be fired set number of times
 */
public class RepeatingSchedulableJob implements Serializable {
    private static final long serialVersionUID = 1L;

    private MotechEvent motechEvent;
    private Date startTime;
    private Date endTime;
    private Integer repeatCount;
    private Long repeatIntervalInMilliSeconds;

    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime,
                                   final Integer repeatCount, final Long repeatIntervalInMilliSeconds) {
        this.motechEvent = motechEvent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatCount = repeatCount;
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
    }

    public RepeatingSchedulableJob(final MotechEvent motechEvent, final Date startTime, final Date endTime,
                                   final Long repeatIntervalInMilliSeconds) {
        this(motechEvent, startTime, endTime, null, repeatIntervalInMilliSeconds);
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public void setMotechEvent(final MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(final Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Long getRepeatInterval() {
        return repeatIntervalInMilliSeconds;
    }

    public void setRepeatInterval(final Long repeatIntervalInMilliSeconds) {
        this.repeatIntervalInMilliSeconds = repeatIntervalInMilliSeconds;
    }

    @Override
    public String toString() {
        return "RepeatingSchedulableJob [motechEvent=" + motechEvent
                + ", startTime=" + startTime + ", endTime=" + endTime
                + ", repeatCount=" + repeatCount + ", repeatIntervalInMilliSeconds="
                + repeatIntervalInMilliSeconds + "]";
    }

}
