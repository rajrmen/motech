package org.motechproject.testing.utils.osgi;

import org.apache.log4j.Logger;
import org.eclipse.gemini.blueprint.test.AbstractConfigurableBundleCreatorTests;
import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.testing.utils.MavenArtifact;
import org.motechproject.testing.utils.MavenDependencyListParser;
import org.osgi.framework.Constants;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BaseOsgiIT extends AbstractConfigurableBundleCreatorTests {

    private Logger logger = Logger.getLogger(BaseOsgiIT.class);

    public static final String PLATFORM_NAME = Platforms.FELIX;
    private static final String SPRING_GROUP_ID = "org.springframework";
    private static final String SPRING_OSGI_GROUP_ID = SPRING_GROUP_ID + ".osgi";
    private static final String SPRING_CONTEXT_ID = SPRING_GROUP_ID + ".context";
    private static final String SPRING_OSGI_CORE_ID = SPRING_OSGI_GROUP_ID + ".core";
    private static final String ECLIPSE_OSGI = "org.eclipse.osgi";
    private static final String BACKPORT_GROUP_ID = "edu.emory.mathcs.backport";
    private static final String SPRING_OSGI_VERSION_PROP_KEY = "spring.osgi.version";
    private static final String SPRING_VERSION_PROP_KEY = "spring.version";

    private String defaultDependenciesListFilename = "modules/osgi-it/osgi-it/target/dependencies.list";

    public void testOsgiPlatformStarts() throws Exception {
        logger.info(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        logger.info(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        logger.info(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
    }

    @Override
    protected String getPlatformName() {
        return PLATFORM_NAME;
    }

    @Override
    protected Resource getTestingFrameworkBundlesConfiguration() {
        final Resource testingFrameworkBundlesConfiguration = super.getTestingFrameworkBundlesConfiguration();
        logger.debug(testingFrameworkBundlesConfiguration);
        return testingFrameworkBundlesConfiguration;
    }


    @Override
    protected String[] getTestBundlesNames() {
        List<MavenArtifact> artifacts;
        try {
            artifacts = MavenDependencyListParser.parseDependencies(new FileSystemResource(getDefaultDependenciesListFilename()));
        } catch (IOException e) {
            String error = "Error loading the dependency list resource: " + getTestingFrameworkBundlesConfiguration();
            logger.error(error, e);
            throw new IllegalArgumentException(error, e);
        }
        logger.info("Maven artifacts " + artifacts);
        String springBundledVersion = null;
        String springOsgiVersion = null;
        Iterator<MavenArtifact> iter = artifacts.iterator();

        while (iter.hasNext()) {
            MavenArtifact artifact = iter.next();
            if (artifact.getGroupId().equals(SPRING_GROUP_ID) && artifact.getArtifactId().equals(SPRING_CONTEXT_ID)) {
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

        String[] bundles = new String[artifacts.size()];
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

        bundles = StringUtils.sortStringArray(bundles);
        logger.info("Default framework bundles :" + ObjectUtils.nullSafeToString(bundles));
        logger.debug("TEST BUNDLES " + Arrays.asList(bundles));
        return bundles;
    }

    @Override
    protected String[] getTestFrameworkBundlesNames() {
        final String[] testFrameworkBundlesNames = super.getTestFrameworkBundlesNames();
        logger.debug(Arrays.asList(testFrameworkBundlesNames));

        String[] bundles = {
                "org.junit,com.springsource.org.junit,4.9.0",
                "org.apache.log4j,com.springsource.org.apache.log4j,1.2.16",
                "org.slf4j,slf4j-api,1.6.4",
                "org.slf4j,slf4j-log4j12,1.6.4",
                "org.slf4j,jcl-over-slf4j,1.6.4",
                "org.aopalliance,com.springsource.org.aopalliance,1.0.0",
                "org.objectweb.asm,com.springsource.org.objectweb.asm,2.2.3",
                "org.springframework,spring-asm,3.1.0.RELEASE",
                "org.springframework,spring-beans,3.1.0.RELEASE",
                "org.springframework,spring-core,3.1.0.RELEASE",
                "org.springframework,spring-context,3.1.0.RELEASE",
                "org.springframework,spring-expression,3.1.0.RELEASE",
                "org.springframework,spring-aop,3.1.0.RELEASE",
                "org.springframework,spring-test,3.1.0.RELEASE",
                "org.eclipse.gemini,org.eclipse.gemini.blueprint.io,1.0.2.RELEASE",
                "org.eclipse.gemini,org.eclipse.gemini.blueprint.core,1.0.2.RELEASE",
                "org.eclipse.gemini,org.eclipse.gemini.blueprint.extender,1.0.2.RELEASE",
                "org.eclipse.gemini.blueprint,org.motechproject.gemini-blueprint-test,1.0.2.RELEASE"};

        return bundles;
    }

    public String getDefaultDependenciesListFilename() {
        return defaultDependenciesListFilename;
    }
}






