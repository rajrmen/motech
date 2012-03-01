package org.motechproject.mobileforms.api.utils;

import static ch.lambdaj.Lambda.join;
import static java.util.Arrays.asList;

import java.io.File;

import org.motechproject.MotechException;
import org.springframework.core.io.ClassPathResource;

public class IOUtils {
    public static final String XFORMS_FOLDER = "xforms";

    public String getFileContent(String fileName, String formGroupName) {
        String xformFilePath = join(asList(XFORMS_FOLDER, formGroupName, fileName), File.separator);
        try {
        	ClassPathResource resource = new ClassPathResource(xformFilePath);
        	if (resource.exists()) {
        		return org.apache.commons.io.IOUtils.toString(resource.getInputStream());
        	}
        	throw new MotechException("Could not find form with path: " + xformFilePath);
        } catch (Exception e) {
            throw new MotechException("Encountered error while loading openxdata forms", e);
        }
    }
}
