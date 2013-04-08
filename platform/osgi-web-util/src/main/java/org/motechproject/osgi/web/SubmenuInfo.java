package org.motechproject.osgi.web;

public class SubmenuInfo {

    private String url;
    private boolean needsAttention;

    public boolean isNeedsAttention() {
        return needsAttention;
    }

    public void setNeedsAttention(boolean needsAttention) {
        this.needsAttention = needsAttention;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SubmenuInfo(String url) {
        this.url = url;
    }
}
