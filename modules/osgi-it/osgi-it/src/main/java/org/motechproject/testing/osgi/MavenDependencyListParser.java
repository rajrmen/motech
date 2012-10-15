package org.motechproject.testing.osgi;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class MavenDependencyListParser {

    private MavenDependencyListParser() {
    }

    public static List<MavenArtifact> parseDependencies(final Resource resource) throws IOException {
        return parseDependencies(new InputStreamReader(resource.getInputStream()));
    }

    public static List<MavenArtifact> parseDependencies(final InputStreamReader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        List<MavenArtifact> artifacts = new ArrayList<>();
        String line = in.readLine();
        while (line != null) {
            if (isSpecLine(line)) {
                artifacts.add(MavenArtifact.parse(line));
            }
            line = in.readLine();
        }
        return artifacts;
    }

    private static boolean isSpecLine(final String line) {
        if (line.contains("junit:junit")) {
            return false;
        }
        if (line.contains("org.springframework.test:")) {
            return false;
        }
        if (line.contains(":org.springframework.osgi.test:")) {
            return false;
        }
        if (line.contains("org.springframework.asm:")) {
            return false;
        }
        if (line.contains("org.springframework.aop:")) {
            return false;
        }
        if (line.contains("org.springframework.beans:")) {
            return false;
        }
        if (line.contains("org.springframework.context:")) {
            return false;
        }
        if (line.contains("org.springframework.core:")) {
            return false;
        }
        if (line.contains("org.springframework.expression:")) {
            return false;
        }
        if (line.contains("project: MavenProject:")) {
            return false;
        }

        if (BaseOsgiIT.PLATFORM_NAME.contains("Felix")) {
            if (line.contains("eclipse")) {
                return false;
            }
            if (line.contains("org.osgi.core")) {
                return false;
            }
            if (line.contains(":org.apache.felix.main:")) {
                return false;
            }
            if (line.contains("org.apache.felix:org.apache.felix.framework:jar:")) {
                return false;
            }
            if (line.contains("org.springframework.osgi.core:jar:1.2.1:")) {
                return false;
            }
            if (line.contains("org.springframework.osgi:org.springframework.osgi.io:jar:1.2.1")) {
                return false;
            }
            if (line.endsWith(":org.eclipse.osgi:")) {
                return false;
            }

        } else {
            if (line.contains("org.springframework.osgi.core:")) {
                return false;
            }

            if (line.contains("org.springframework.osgi.io:")) {
                return false;
            }
        }

        int i = line.indexOf(':');
        if (i > 0) {
            // check for a second colon.
            i = line.indexOf(':', i + 1);
            if (i > 0) {
                // check for a third colon
                return (line.indexOf(':', i + 1) >= 0);
            }
        }
        return false;
    }
}
