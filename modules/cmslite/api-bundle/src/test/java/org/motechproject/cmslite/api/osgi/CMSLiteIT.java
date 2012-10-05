package org.motechproject.cmslite.api.osgi;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.test.platform.Platforms;
import org.springframework.osgi.util.OsgiStringUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
import java.util.jar.Manifest;

public class CMSLiteIT extends AbstractConfigurableBundleCreatorTests {

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

    private Logger log = Logger.getLogger(this.getClass());

    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;
    }

    @Test
    public void testOsgiPlatformStarts() {
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

        /*final ServiceReference serviceReference = bundleContext.getServiceReference("org.motechproject.cmslite.api.service.CMSLiteService");
        System.out.print("SERVICE REFERENCE ---------- " + serviceReference + " \n");

        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            System.out.print(OsgiStringUtils.nullSafeSymbolicName(bundles[i]));
            System.out.print("\n ");
        }
        System.out.print("\n");*/
    }


    /*protected String[] getTestBundlesNames() {
        //System.out.println(Arrays.asList(super.getTestBundlesNames()));
        return new String[] {
            "org.motechproject,motech-cmslite-api,0.14-SNAPSHOT",
            "org.codehaus.jackson,org.motechproject.org.codehaus.jackson,1.9.7",
            "joda-time,joda-time,2.0",
            "org.ektorp,org.motechproject.org.ektorp,1.3.0",
            "net.sourceforge.cglib,com.springsource.net.sf.cglib,2.2.0",
            "org.apache.httpcomponents,com.springsource.org.apache.httpcomponents.httpcore,4.2.0",
            "org.apache.httpcomponents,com.springsource.org.apache.httpcomponents.httpclient,4.2.0",
            "org.apache.httpcomponents,com.springsource.org.apache.httpcomponents.httpclient-cache,4.2.0",
            "org.apache.commons,com.springsource.org.apache.commons.codec,1.6.0",
            "net.sourceforge.ehcache,com.springsource.net.sf.ehcache,2.2.0",
            "org.codehaus.btm,com.springsource.org.codehaus.btm,1.3.3",
            "javax.jms,com.springsource.javax.jms,1.1.0",
            "org.hibernate,com.springsource.org.hibernate,3.3.2.GA",
            "org.antlr,com.springsource.antlr,2.7.7",
            "org.jboss.javassist,com.springsource.javassist,3.15.0.GA",
            "org.apache.commons,com.springsource.org.apache.commons.collections,3.2.1",
            "org.dom4j,com.springsource.org.dom4j,1.6.1",
            "javax.xml.stream,com.springsource.javax.xml.stream,1.0.1",
            "org.jsoup,com.springsource.org.jsoup,1.5.2",
            "org.jruby,com.springsource.org.jruby,1.4.0",
            "org.springframework,org.springframework.instrument,3.1.0.RELEASE",
            "org.motechproject,motech-platform-common-api,0.14-SNAPSHOT",
            "org.motechproject,motech-platform-event,0.14-SNAPSHOT",
            "org.activemq,org.motechproject.org.activemq,5.4.2",
            "com.google.code.gson,gson,1.7.1",
            "com.thoughtworks.xstream,org.motechproject.com.thoughtworks.xstream,1.4.2",
            "com.bea.xml.stream,org.motechproject.com.bea.xml.stream,1.2.0",
            "org.codehaus.woodstox,com.springsource.com.ctc.wstx,3.2.8",
            "com.sun.msv,com.springsource.com.sun.msv,0.0.0.20081113",
        };
    }
*/
    String springOsgiVersion;
    String springBundledVersion;

    /**
     * Get a resource containing the list of dependencies for the current bundle.
     * The dependencies file could be hand crafted, but it is better to use the
     * Maven dependency plugin to generate this file from the POM contents. Override
     * this method to change the file name to something other than the default.
     * @return the dependencies list file as generated by the Maven dependency plugin.
     * @see org.springframework.osgi.test.AbstractDependencyManagerTests#getTestingFrameworkBundlesConfiguration()
     */
    @Override
    protected Resource getTestingFrameworkBundlesConfiguration() {
        return new FileSystemResource(DEFAULT_DPENDENCIES_LIST_FILENAME);
    }

    /**
     * Returns the names of the test bundles to load. Overrides the spring method
     * to replace the properties file configuration with configuration from the
     * output of the Maven dependency plugin.
     * @return the bundle names as CSV values containing the maven groupId, artifactId, and version.
     * @see org.springframework.osgi.test.AbstractDependencyManagerTests#getTestFrameworkBundlesNames()
     * @throws IllegalArgumentException if the dependency list resource retrieved from the
     *   {@link #getTestingFrameworkBundlesConfiguration()} method throws an IOException.
     */
    @Override
    protected String[] getTestFrameworkBundlesNames() {
        List<MavenArtifact> artifacts = null;
        try {
            artifacts = MavenDependencyListParser.parseDependencies(getTestingFrameworkBundlesConfiguration());
        } catch (IOException e) {
            String error = "Error loading the dependency list resource: " + getTestingFrameworkBundlesConfiguration();
            log.error(error, e);
            throw new IllegalArgumentException(error, e);
        }

        if (log.isTraceEnabled()) {
            log.trace("Maven artifacts " + artifacts);
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
        String[] bundles = new String[artifacts.size()];
        iter = artifacts.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            MavenArtifact artifact = iter.next();

            bundles[i] = artifact.getGroupId() + "," + artifact.getArtifactId() + ","
                    + artifact.getVersion();
        }

        // pass properties to test instance running inside OSGi space
        if (springOsgiVersion != null)
        System.getProperties().put(SPRING_OSGI_VERSION_PROP_KEY, springOsgiVersion);
        if (springBundledVersion != null)
        System.getProperties().put(SPRING_VERSION_PROP_KEY, springBundledVersion);

        // sort the array (as the dependency list can be any order)
        // Is this really required??
        bundles = StringUtils.sortStringArray(bundles);
        if (log.isDebugEnabled()) {
            log.debug("Default framework bundles :" + ObjectUtils.nullSafeToString(bundles));
        }

        System.out.print("TEST BUNDLES " + Arrays.asList(bundles));
        return bundles;
    }


/*

    protected String[] getTestFrameworkBundlesNames1() {
        System.out.println(Arrays.asList(super.getTestFrameworkBundlesNames()));
        return new String[] {
                "org.springframework.osgi, spring-osgi-test,1.2.1",
                "org.junit,com.springsource.org.junit,4.8.2",
                "org.slf4j,com.springsource.slf4j.org.apache.commons.logging,1.5.0",
                "org.slf4j,com.springsource.slf4j.api,1.6.1",
                "org.slf4j,com.springsource.slf4j.simple,1.6.1",
                "org.springframework,org.springframework.beans,3.1.0.RELEASE",
                "org.springframework,org.springframework.core,3.1.0.RELEASE",
                "org.springframework,org.springframework.context,3.1.0.RELEASE",
                "org.springframework.osgi, org.springframework.osgi.core,1.2.1",
                "org.aopalliance,com.springsource.org.aopalliance,1.0.0",
                "org.springframework,org.springframework.aop,3.1.0.RELEASE",
                "org.springframework.osgi,org.springframework.osgi.io,1.2.1",
                "org.springframework,org.springframework.test,3.1.0.RELEASE",
                "org.objectweb.asm,com.springsource.org.objectweb.asm,3.2.0",
                "org.objectweb.asm,com.springsource.org.objectweb.asm,1.5.3",
                "org.springframework,org.springframework.asm,3.1.0.RELEASE",
                "org.apache.commons,com.springsource.org.apache.commons.pool,1.5.3",
                "org.apache.log4j,com.springsource.org.apache.log4j,1.2.15",
                "org.springframework,org.springframework.expression,3.1.0.RELEASE",
                "javax.servlet,com.springsource.javax.servlet,2.5.0",
                "org.motechproject,motech-cmslite-api-bundle,0.14-SNAPSHOT",
                "commons-io,commons-io,2.4",
                "org.beanshell,com.springsource.bsh,2.0.0.b4",
                "org.aspectj,com.springsource.org.aspectj.weaver,1.6.0.m2",
                "org.aspectj,com.springsource.org.aspectj.runtime,1.6.0.m2",
                "com.jamonapi,com.springsource.com.jamonapi,2.4.0",
                "edu.emory.mathcs.backport,com.springsource.edu.emory.mathcs.backport,3.1.0",
                "org.codehaus.groovy,com.springsource.org.codehaus.groovy,1.7.3",
                "javax.el,com.springsource.javax.el,2.1.0",
                "javax.inject,com.springsource.javax.inject,1.0.0",
                "javax.xml.rpc,com.springsource.javax.xml.rpc,1.1.0",
                "javax.activation,com.springsource.javax.activation,1.1.0",
                "javax.ejb,com.springsource.javax.ejb,3.0.0",
                "javax.xml.soap,com.springsource.javax.xml.soap,1.3.0",
                "javax.persistence,com.springsource.javax.persistence,2.0.0",
                "javax.validation,com.springsource.javax.validation,1.0.0.GA",
                "net.sourceforge.jopt-simple,com.springsource.joptsimple,3.0.0",
                "org.hibernate,com.springsource.org.hibernate.validator,4.2.0.Final",
                "org.joda,com.springsource.org.joda.time,1.6.0",
                "org.apache.commons,com.springsource.org.apache.commons.lang,2.1.0",
                "com.sun.msv,com.springsource.com.sun.msv.datatype,0.0.0.20060615",
                "org.apache.xerces,com.springsource.org.apache.xerces,2.9.1",
                "org.apache.xml,com.springsource.org.apache.xml.resolver,1.2.0",
                "org.apache.xmlcommons,com.springsource.org.apache.xmlcommons,1.3.4",
                "org.relaxng,com.springsource.org.relaxng.datatype,1.0.0",
                "net.sourceforge.iso-relax,com.springsource.org.iso_relax.verifier,0.0.0.20041111",
                "nu.xom,com.springsource.nu.xom,1.2.5",
        };
    }
*/

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

    /**
     * Private to prevent instantiation.
     */
    private MavenDependencyListParser() {
        super();
    }

    /**
     * Parse the Maven dependencies list specified as a Spring resource.
     * @param resource the Spring resource pointing to the dependencies list.
     * @return the artifacts from the dependency list.
     * @throws IOException if the resource cannot be read.
     */
    public static List<MavenArtifact> parseDependencies(final Resource resource)
            throws IOException {
        return parseDependencies(new InputStreamReader(resource.getInputStream()));
    }

    /**
     * Parse the Maven dependencies list specified as a {@link Reader}.
     * @param reader the reader to use for reading the dependencies list.
     * @return the artifacts from the dependency list.
     * @throws IOException if the resource cannot be read.
     */
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

    /**
     * A spec line is a line specifying a Maven artifact. For it to be valid
     * there must be at least three colon characters on the line.
     * @param line the line to check.
     * @return true if it is a spec line.
     */
    private static boolean isSpecLine(final String line) {
        if (line.contains("org.apache.felix.framework:")) return false;
        if (line.contains("org.springframework.aop:")) return false;
        if (line.contains("org.springframework.asm:")) return false;
        if (line.contains("org.springframework.beans:")) return false;
        if (line.contains("org.springframework.context:")) return false;
        if (line.contains("org.springframework.context.support:")) return false;
        if (line.contains("org.springframework.core:")) return false;
        if (line.contains("org.springframework.expression:")) return false;
        if (line.contains("org.springframework.osgi.core:")) return false;
        if (line.contains("org.springframework.osgi.io:")) return false;
        if (line.contains("org.springframework.osgi.test:")) return false;
        if (line.contains("project: MavenProject:")) return false;
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
