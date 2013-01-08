package org.motechproject.mrs;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.DataProviderLookup;
import org.motechproject.commons.api.MotechException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

public class MRSDataProvider implements DataProviderLookup {
    private String body;

    @Autowired
    public MRSDataProvider(final ResourceLoader resourceLoader)  {
        setBody(resourceLoader);
    }

    @Override
    public String toJSON() {
        return body;
    }

    @Override
    public Object lookup(String clazz, Map<String, String> lookupFields) {
        return null;
    }

    @Override
    public boolean supports(String clazz) {
        return Objects.equals(clazz, MRSPatient.class.getName()) ||
                Objects.equals(clazz, MRSPerson.class.getName()) ||
                Objects.equals(clazz, MRSFacility.class.getName());
    }

    private void setBody(final ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");
        String content = "";

        if (resource != null) {
            StringWriter writer = new StringWriter();
            InputStream is = null;

            try {
                is = resource.getInputStream();

                IOUtils.copy(is, writer);
                content = writer.toString().replaceAll("\\s+", "");
            } catch (IOException e) {
                throw new MotechException("Can't read from stream", e);
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(writer);
            }
        }

        this.body = content;
    }
}
