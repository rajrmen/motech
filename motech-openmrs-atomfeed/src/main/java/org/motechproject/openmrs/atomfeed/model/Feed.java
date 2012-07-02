package org.motechproject.openmrs.atomfeed.model;

import java.util.ArrayList;
import java.util.List;

public class Feed {
    String updated;
    String versionId;
    String title;
    List<Link> link = new ArrayList<Link>();
    List<Entry> entry = new ArrayList<Entry>();

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    public void add(Entry entry) {
        this.entry.add(entry);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<Link> getLink() {
        return link;
    }

    public void setLink(List<Link> link) {
        this.link = link;
    }
    
    public void add(Link link) {
        this.link.add(link);
    }
}
