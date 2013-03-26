package org.motechproject.cmslite.api.web;

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Predicate;
import org.junit.Test;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.junit.Assert.assertEquals;

public class ResourceFilterTest {

    @Test
    public void shouldReturnAll() {
        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);
        List<ResourceDto> actual = ResourceFilter.filter("", true, true, "", contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOnlyStringResources() {
        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);

        CollectionUtils.filter(expected, new Predicate() {
            @Override
            public boolean evaluate(Object arg) {
                return arg instanceof ResourceDto && !equalsIgnoreCase(((ResourceDto) arg).getType(), "stream");
            }
        });

        List<ResourceDto> actual = ResourceFilter.filter("", true, false, "", contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOnlyStreamResources() {
        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);

        CollectionUtils.filter(expected, new Predicate() {
            @Override
            public boolean evaluate(Object arg) {
                return arg instanceof ResourceDto && !equalsIgnoreCase(((ResourceDto) arg).getType(), "string");
            }
        });

        List<ResourceDto> actual = ResourceFilter.filter("", false, true, "", contents);

        assertEquals(expected, actual);
    }

    private List<Content> getContents() {
        List<Content> list = new ArrayList<>();

        list.add(new StringContent("english", "string-file-1", "some valid value"));
        list.add(new StringContent("spanish", "string-file-2", "some valid value"));
        list.add(new StringContent("german", "string-file-3", "some valid value"));

        list.add(new StreamContent("polish", "stream-file-1", null, "checksum", "contentType"));
        list.add(new StreamContent("danish", "stream-file-2", null, "checksum", "contentType"));
        list.add(new StreamContent("arabic", "stream-file-3", null, "checksum", "contentType"));

        return list;
    }

    private List<ResourceDto> createInputData(List<Content> contents) {
        List<ResourceDto> list = new ArrayList<>(contents.size());

        for (Content content : contents) {
            list.add(new ResourceDto(content));
        }

        return list;
    }
}
