package org.motechproject.org.ektorp;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.ektorp.DbInfo;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.test.platform.Platforms;
import org.springframework.osgi.util.OsgiStringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EktorpOsgiIT extends AbstractConfigurableBundleCreatorTests {

    /** The path to the resource containing the default bundles to load. */
    protected static final String DEFAULT_DPENDENCIES_LIST_FILENAME = "target/dependencies.list";

    /** Maven groupId for Spring. */
    public static final String SPRING_GROUP_ID = "org.springframework";
    /** Maven groupId for Spring OSGi. */
    public static final String SPRING_OSGI_GROUP_ID = SPRING_GROUP_ID + ".osgi";
    /** Maven artifactId for the Spring context bundle. */
    public static final String SPRING_CONTEXT_ID = SPRING_GROUP_ID + ".context";
    /** Maven artifactId for the Spring OSGi core bundle. */
    public static final String SPRING_OSGI_CORE_ID = SPRING_OSGI_GROUP_ID + ".core";
    /** Maven artifactId for the Spring OSGi Test bundle. */
    public static final String SPRING_OSGI_TEST_ARTIFACT_ID = SPRING_OSGI_GROUP_ID + ".test";
    /** Maven groupId and artifactId for the eclipse framework. */
    public static final String ECLIPSE_OSGI = "org.eclipse.osgi";
    /** Maven groupId for the backport software library. */
    public static final String BACKPORT_GROUP_ID = "edu.emory.mathcs.backport";

    /** Store the Spring OSGi version in a system property for the test classes. */
    private static final String SPRING_OSGI_VERSION_PROP_KEY = "spring.osgi.version";
    /** Store the Spring version in a system property for the test classes. */
    private static final String SPRING_VERSION_PROP_KEY = "spring.version";
    public static final String PLATFORM_NAME = Platforms.EQUINOX;

    private Logger log = Logger.getLogger(EktorpOsgiIT.class.getName());

    public EktorpOsgiIT() {
    }

    @Override
    protected String getPlatformName() {
        return PLATFORM_NAME;
    }

    @Override
    protected Properties getDefaultSettings() {
        final Properties defaultSettings = super.getDefaultSettings();
        defaultSettings.put("osgi.debug","true");
        return defaultSettings;
    }

    @Test
    public void testOsgiPlatformStarts() {
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

        final StdCouchDbConnector test = new StdCouchDbConnector("test", new StdCouchDbInstance( new StdHttpClient.Builder().caching(false).build()));
            test.createDatabaseIfNotExists();
            final DbInfo dbInfo = test.getDbInfo();
            System.out.print(dbInfo);
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            System.out.print(OsgiStringUtils.nullSafeSymbolicName(bundles[i]));
            System.out.print("\n ");
        }
        System.out.print("\n");
    }

    String springOsgiVersion;
    String springBundledVersion;

    @Override
    protected Resource getTestingFrameworkBundlesConfiguration() {
        return new FileSystemResource(DEFAULT_DPENDENCIES_LIST_FILENAME);
    }

    @Override
    protected String[] getTestFrameworkBundlesNames() {
        List<MavenArtifact> artifacts = null;
        try {
            artifacts = MavenDependencyListParser.parseDependencies(getTestingFrameworkBundlesConfiguration());
        } catch (IOException e) {
            String error = "Error loading the dependency list resource: " + getTestingFrameworkBundlesConfiguration();
            log.log(Level.SEVERE, error, e);
            throw new IllegalArgumentException(error, e);
        }

        if (log.isLoggable(Level.INFO)) {
            log.info("Maven artifacts " + artifacts);
        }

        // Filter and get versions
        Iterator<MavenArtifact> iter = artifacts.iterator();
        while (iter.hasNext()) {
            MavenArtifact artifact = iter.next();
            if (artifact.getGroupId().equals(SPRING_GROUP_ID)
                    && artifact.getArtifactId().equals(SPRING_CONTEXT_ID)) {
                springBundledVersion = artifact.getVersion();
            } else if (artifact.getGroupId().equals(SPRING_OSGI_GROUP_ID)) {
                if (artifact.getArtifactId().equals(SPRING_OSGI_CORE_ID)) {
                    springOsgiVersion = artifact.getVersion();
                }
            } else if (artifact.getGroupId().equals(ECLIPSE_OSGI)
                    && artifact.getArtifactId().equals(ECLIPSE_OSGI)) {
                // filter out this since it is started by the framework specification in
                // the POM.
                iter.remove();
            } else if (artifact.getGroupId().equals(BACKPORT_GROUP_ID)
                    && JdkVersion.isAtLeastJava15()) {
                // Filter out backport if Java is 1.5 or higher.
                iter.remove();
            }
        }

        // Copy the filtered artifacts to the bundle array that we need
        // to return from this method.
            String[] bundles = new String[artifacts.size()+2];
        iter = artifacts.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            MavenArtifact artifact = iter.next();

            bundles[i] = artifact.getGroupId() + "," + artifact.getArtifactId() + ","
                    + artifact.getVersion();
        }

        // pass properties to test instance running inside OSGi space
        if (springOsgiVersion != null) {
            System.getProperties().put(SPRING_OSGI_VERSION_PROP_KEY, springOsgiVersion);
        }
        if (springBundledVersion != null) {
            System.getProperties().put(SPRING_VERSION_PROP_KEY, springBundledVersion);
        }

        // sort the array (as the dependency list can be any order)
        // Is this really required??
        bundles[artifacts.size()] = "org.ektorp,org.motechproject.org.ektorp,1.3.0";
        bundles[artifacts.size()+1] = "org.apache.httpcomponents,com.springsource.org.apache.httpcomponents.httpclient,4.1.1";
        bundles = StringUtils.sortStringArray(bundles);
        if (log.isLoggable(Level.INFO)) {
            log.info("Default framework bundles :" + ObjectUtils.nullSafeToString(bundles));
        }
        System.out.print("TEST BUNDLES " + Arrays.asList(bundles));
        return bundles;
    }

    @Override
    protected Manifest getManifest() {
        return super.getManifest();
    }

    @Override
    protected boolean createManifestOnlyFromTestClass() {
        return false;
    }
}

final class MavenDependencyListParser {

    private MavenDependencyListParser() {
        super();
    }

    public static List<MavenArtifact> parseDependencies(final Resource resource)
            throws IOException {
        return parseDependencies(new InputStreamReader(resource.getInputStream()));
    }

    public static List<MavenArtifact> parseDependencies(final Reader reader)
            throws IOException {
        BufferedReader in = new BufferedReader(reader);
        ArrayList<MavenArtifact> artifacts = new ArrayList<MavenArtifact>();
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
        if (line.contains("org.springframework.osgi.core:")) {
            return false;
        }
        if (line.contains("org.springframework.osgi.io:")) {
            return false;
        }
        if (line.contains("org.springframework.osgi.test:")) {
            return false;
        }
        if (EktorpOsgiIT.PLATFORM_NAME.equals(Platforms.FELIX)) {
        /*if (line.contains("org.springframework.aop:")) {
            return false;
        }

        if (line.contains("org.springframework.beans:")) {
            return false;
        }
        if (line.contains("org.springframework.context:")) {
            return false;
        }
        if (line.contains("org.springframework.context.support:")) {
            return false;
        }
        if (line.contains("org.springframework.core:")) {
            return false;
        }
        if (line.contains("org.springframework.expression:")) {
            return false;
        }*/
        //if (line.endsWith(":test") && !line.contains(":bundle:")) return false;


            if (line.contains("org.springframework.asm:")) {
                return false;
            }
            if (line.contains("org.apache.felix.framework:")) {
                return false;
            }
        }
        if (line.contains("project: MavenProject:")) {
            return false;
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


/**
 * A maven artifact information container. Holds the maven artifact
 * info. Once an instance of this object is created it cannot be changed.
 * @author dlaidlaw
 *
 */
class MavenArtifact {
    /** The artifact groupId. */
    private String groupId;
    /** The artifact artifactId. */
    private String artifactId;
    /** The artifact type (jar, war, zip, etc). */
    private String type;
    /** The artifact version. */
    private String version;
    /** The artifact scope. */
    private String scope;

    /** The string value of this object, cached so it does not need to be
     * continuously recalculated.
     */
    private transient String stringValue;

    /**
     * Default constructor. All values are null.
     */
    private MavenArtifact() {
        super();
    }

    /**
     * Full constructor, All values are provided.
     * @param groupId the artifact groupId.
     * @param artifactId the artifact ID.
     * @param type the artifact type (jar, war, zip, etc).
     * @param version the artifact version.
     * @param scope the artifact scope (compile, test, etc);
     */
    public MavenArtifact(final String groupId, final String artifactId,
                         final String type, final String version, final String scope) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = type;
        this.version = version;
        this.scope = scope;
    }

    /**
     * Parse a maven artifact specification as output by the Maven dependency plugin
     * resolve goal.
     * @param spec the specification string formatted as
     *   groupId:artifactId:type:version:scope:other.
     * @return the artifact.
     * @throws IllegalArgumentException if the specification string does not contain
     *   the required parts with colon separators.
     */
    public static MavenArtifact parse(final String spec) {
        String[] parts = spec.trim().split(":");
        // CHECKSTYLE:OFF the number four is not magic.
        if (parts.length < 4) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException(
                    "The specification must contain at least 5 parts separated by a colon (:)."
                            + " The parts are: groupId:artifactId:type:version:scope:other");
        }

        MavenArtifact ma = new MavenArtifact();
        if (parts[0].length() > 0) {
            ma.groupId = parts[0].replaceAll(".* = ","");
        }
        if (parts[1].length() > 0) {
            ma.artifactId = parts[1];
        }
        if (parts[2].length() > 0) {
            ma.type = parts[2];
        }
        // CHECKSTYLE:OFF the numbers three and four are not magic.
        if (parts[3].length() > 0) {
            ma.version = parts[3];
        }
        if (parts.length >= 4 && parts[4].length() > 0) {
            ma.scope = parts[4];
        }
        // CHECKSTYLE:ON

        return ma;
    }

    /**
     * Output the values separated by colons exactly as the
     * parse method would like to see them. Null values are
     * output as the empty string.
     * @return the string representation of this object.
     */
    @Override
    public String toString() {
        if (stringValue == null) {
            stringValue = asString(groupId) + ":" + asString(artifactId) + ":"
                    + asString(type) + ":" + asString(version)
                    + ":" + asString(scope);
        }
        return stringValue;
    }

    /**
     * Return "" if the argument is null.
     * @param s the string to output.
     * @return "" if s is null, or s otherwise.
     */
    private String asString(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    /**
     * Check if the other object is an instance of this
     * class and that all its parts equal the parts in
     * this instance.
     * @param other the object to test against this object.
     * @return true if the other object is an instance of
     *   this class and all its parts equals the parts
     *   of this class.
     */
    @Override
    public boolean equals(final Object other) {
        if ((other == null) || !(other instanceof MavenArtifact)) {
            return false;
        }
        MavenArtifact o = (MavenArtifact) other;
        return (isEqual(this.groupId, o.getGroupId())
                && isEqual(this.artifactId, o.getArtifactId())
                && isEqual(this.type, o.getType())
                && isEqual(this.version, o.getVersion())
                && isEqual(this.scope, o.getScope()));
    }

    /**
     * Compare this object to the other object, returning true if the
     * other object's groupId and artifactId are equal to this object's
     * groupId and artifactId.
     * @param other the object to compare to this one.
     * @return true if the other equals this object on the two
     *   id properties.
     */
    public boolean idsEqual(final MavenArtifact other) {
        if (other == null) {
            return false;
        }
        return isEqual(this.groupId, other.getGroupId())
                && isEqual(this.artifactId, other.getArtifactId());
    }

    /**
     * Output the hash code for this object.
     * @return the hashCode calculated as the hashCode of
     *   the string returned by the toString method.
     * @see #toString()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Both strings are null or they are equal.
     * @param a one string.
     * @param b the other string.
     * @return true if both are null, or both are equal.
     */
    private boolean isEqual(final String a, final String b) {
        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * Get the groupId.
     * @return the groupId.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Get the artifactId.
     * @return the artifactId.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Get the artifact type (jar, war, zip, etc).
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the version.
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the scope.
     * @return the scope.
     */
    public String getScope() {
        return scope;
    }
}

