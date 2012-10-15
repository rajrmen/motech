package org.motechproject.testing.osgi;

import org.eclipse.gemini.blueprint.test.AbstractConfigurableBundleCreatorTests;
import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.osgi.framework.Constants;
import org.springframework.core.JdkVersion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseOsgiIT extends AbstractConfigurableBundleCreatorTests {
    private Logger logger = Logger.getLogger(BaseOsgiIT.class.getName());

    public static final String PLATFORM_NAME = Platforms.EQUINOX;


    private static final String SPRING_GROUP_ID = "org.springframework";
    private static final String SPRING_OSGI_GROUP_ID = SPRING_GROUP_ID + ".osgi";
    private static final String SPRING_CONTEXT_ID = SPRING_GROUP_ID + ".context";
    private static final String SPRING_OSGI_CORE_ID = SPRING_OSGI_GROUP_ID + ".core";
    private static final String ECLIPSE_OSGI = "org.eclipse.osgi";
    private static final String BACKPORT_GROUP_ID = "edu.emory.mathcs.backport";
    private static final String SPRING_OSGI_VERSION_PROP_KEY = "spring.osgi.version";
    private static final String SPRING_VERSION_PROP_KEY = "spring.version";

    private String defaultDependenciesListFilename = "target/dependencies.list";

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
        logger.info(testingFrameworkBundlesConfiguration.toString());
        return testingFrameworkBundlesConfiguration;
    }


    @Override
    protected String[] getTestBundlesNames() {
        List<MavenArtifact> artifacts;
        try {
            artifacts = MavenDependencyListParser.parseDependencies(new FileSystemResource(getDefaultDependenciesListFilename()));
        } catch (IOException e) {
            String error = "Error loading the dependency list resource: " + getTestingFrameworkBundlesConfiguration();
            logger.log(Level.SEVERE, error, e);
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

        String[] bundles = new String[artifacts.size() + 1];
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
        bundles[bundles.length-1] = getArtifactString(getPomPath());

        bundles = StringUtils.sortStringArray(bundles);
        logger.info("Default framework bundles :" + ObjectUtils.nullSafeToString(bundles));
        logger.info("TEST BUNDLES " + Arrays.asList(bundles));
        return bundles;
    }

    @Override
    protected String[] getTestFrameworkBundlesNames() {
        final String[] testFrameworkBundlesNames = super.getTestFrameworkBundlesNames();
        logger.info(Arrays.asList(testFrameworkBundlesNames).toString());

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
                "org.eclipse.gemini.blueprint,org.motechproject.gemini-blueprint-test,1.0.2.RELEASE",
                "org.motechproject,org.motechproject.osgi.it,0.14-SNAPSHOT"};
        return bundles;
    }

    public String getDefaultDependenciesListFilename() {
        return defaultDependenciesListFilename;
    }
    protected String getPomPath() {
        return getPomPath(this.getClass().getResource(".").getPath()) + "/pom.xml";
    }
    private String getArtifactString(String pomFilePath) {
        try {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringComments(true);
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document pomDoc = documentBuilder.parse(pomFilePath);
        //pomDoc.getDocumentElement().getElementsByTagName("groupId").item(0).getChildNodes().item(0)
        final String groupId = getPomInfo(pomDoc, "groupId");
        final String artifactId = getPomInfo(pomDoc, "artifactId");
        final String version = getPomInfo(pomDoc, "version");
        return String.format("%s,%s,%s", groupId, artifactId, version);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get artifact details from pom.xml " + pomFilePath);
        }
    }

    private String getPomInfo(Document pomDoc, String tagName) {
        final Element root = pomDoc.getDocumentElement();
        final Node node = getChild(root, tagName);
        return getNodeContent(node, Node.TEXT_NODE);
    }

    private String getNodeContent(Node node, short textNode) {
        final NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node item = nodes.item(i);
            return item.getNodeValue().trim();
        }
        return null;
    }

    private Node getChild(Element root, String tagName) {
        final NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node item = nodes.item(i);
            if (tagName.equals(item.getNodeName())) return item;
        }
        return null;
    }

    private String getPomPath(String path) {
        if (path == null) return null;
        if (new File(path + "/pom.xml").exists()) return path;
        return getPomPath(new File(path).getParent());
    }
}






