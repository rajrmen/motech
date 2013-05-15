package org.motechproject.email.model;

import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: srikanthnutigattu
 * Date: 14/05/13
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class MailDetail {
    private static final int DEFAULT_TIMEOUT_MINS = 60;

    private String text;
    private String moduleName;
    private DateTime date = DateTime.now();
    private DateTime timeout = DateTime.now().plusMinutes(DEFAULT_TIMEOUT_MINS);

    public MailDetail(String text, String moduleName) {
        this.text = text;
        this.moduleName = moduleName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public DateTime getTimeout() {
        return timeout;
    }

    public void setTimeout(DateTime timeout) {
        this.timeout = timeout;
    }


    public DateTime getDate() {
        return date;
    }
}
