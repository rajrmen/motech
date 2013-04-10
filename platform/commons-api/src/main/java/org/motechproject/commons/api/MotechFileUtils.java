package org.motechproject.commons.api;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class MotechFileUtils {

    private static final String PROPERTIES = ".properties";

    public static List<File> recursivelyListAllFiles(File directory, FilenameFilter filenameFilter) {
        List<File> propertyFiles = new ArrayList<>();

        File[] allFiles = directory.listFiles();
        for (File current : allFiles) {
            if (current.isDirectory()) {
                propertyFiles.addAll(recursivelyListAllFiles(current, filenameFilter));
            } else {
                if (filenameFilter.accept(current, current.getName())) {
                    propertyFiles.add(current);
                }
            }
        }
        return propertyFiles;
    }

    public static List<File> recursivelyListAllFiles(File directory) {
        return recursivelyListAllFiles(directory, new FileExtensionFilter());
    }

    public static List<File> recursivelyListAllPropertyFiles(File directory) {
        return recursivelyListAllFiles(directory, new FileExtensionFilter(PROPERTIES));
    }

    private static class FileExtensionFilter implements FilenameFilter {

        private String extension;

        private FileExtensionFilter() {
        }

        private FileExtensionFilter(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean accept(File dir, String name) {
            if (isBlank(extension)) {
                return true;
            }
            return name.endsWith(extension);
        }
    }
}
