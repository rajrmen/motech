package org.motechproject.decisiontree.model;


/**
 *
 */
public class DialPrompt extends Prompt {

    private String phoneNumber;
    private boolean record = false;
    private int limitTime = 1000;
    private int timeout = 30;
    private String musicOnHold = "default";

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Prompt setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
