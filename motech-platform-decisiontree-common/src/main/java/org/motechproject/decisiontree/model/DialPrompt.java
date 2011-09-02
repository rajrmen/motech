package org.motechproject.decisiontree.model;


/**
 *
 */
public class DialPrompt extends Prompt {

    private String[] phoneNumbers;
    private int limitTime = 1000;
    private boolean record = false;
    private int timeout = 30;
    private String musicOnHold = "default";

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }
    public Prompt setPhoneNumbers(String... phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }
}
