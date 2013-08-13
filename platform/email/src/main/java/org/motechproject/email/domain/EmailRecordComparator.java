package org.motechproject.email.domain;

import java.util.Comparator;

/**
 * The <code>EmailRecordComparator</code> class is an implementation of Comparator interface,
 * that allows to compare {@Link EmailRecord} by a single field.
 */

public class EmailRecordComparator implements Comparator<EmailRecord> {
    private String compareField = "subject";
    private Boolean ascending = true;

    public EmailRecordComparator(Boolean ascending, String compareField) {
        this.compareField = compareField;
        this.ascending = ascending;
    }

    @Override
    public int compare(EmailRecord o1, EmailRecord o2) {
        int ret;

        switch (compareField) {
            case "fromAddress":
                ret = (o1.getFromAddress().toLowerCase()).compareTo(o2.getToAddress().toLowerCase());
                break;
            case "toAddress":
                ret = (o1.getToAddress().toLowerCase()).compareTo(o2.getToAddress().toLowerCase());
                break;
            case "message":
                ret = o1.getMessage().compareTo(o2.getMessage());
                break;
            case "deliveryStatus":
                ret = o1.getDeliveryStatus().compareTo(o2.getDeliveryStatus());
                break;
            case "subject":
                ret = (o1.getSubject().toLowerCase()).compareTo(o2.getSubject().toLowerCase());
                break;
            case "deliveryTime":
            default:
                ret = o1.getDeliveryTime().compareTo(o2.getDeliveryTime());
                break;
        }

        return (ascending) ? ret : -ret;
    }

}
