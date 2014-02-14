package org.motechproject.mds.service.impl.internal;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MDSConstructor;
import org.motechproject.osgi.web.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static java.util.jar.Attributes.Name;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.mds.util.ClassName.getEntityName;
import static org.motechproject.mds.util.ClassName.getInterfaceName;
import static org.motechproject.mds.util.ClassName.getRepositoryName;
import static org.motechproject.mds.util.ClassName.getServiceName;
import static org.motechproject.mds.util.Constants.Manifest;

/**
 * Default implementation of {@link org.motechproject.mds.service.JarGeneratorService} interface.
 */
@Service
public class JarGeneratorServiceImpl extends BaseMdsService implements JarGeneratorService {
    private AllEntities allEntities;
    private BundleHeaders bundleHeaders;
    private BundleContext bundleContext;
    private MDSConstructor mdsConstructor;

    @Override
    @Transactional
    public void regenerateMdsDataBundle() {
        File tmpBundleFile;

        try {
            tmpBundleFile = generate();
        } catch (IOException | NotFoundException | CannotCompileException e) {
            throw new MdsException("Unable to generate entities bundle", e);
        }

        Bundle dataBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, createSymbolicName());

        try (InputStream in = new FileInputStream(tmpBundleFile)) {
            if (dataBundle == null) {
                dataBundle = bundleContext.installBundle(bundleLocation(), in);
            } else {
                dataBundle.update(in);
            }

            dataBundle.start();
        } catch (IOException e) {
            throw new MdsException("Unable to read temporary entities bundle", e);
        } catch (BundleException e) {
            throw new MdsException("Unable to start the entities bundle", e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JarGeneratorServiceImpl.class);


    @Override
    @Transactional
    public File generate() throws IOException, NotFoundException, CannotCompileException {
        Path tempDir = Files.createTempDirectory("mds");
        Path tempFile = Files.createTempFile(tempDir, "mds-entities", ".jar");

        java.util.jar.Manifest manifest = createManifest();
        FileOutputStream fileOutput = new FileOutputStream(tempFile.toFile());

        try (JarOutputStream output = new JarOutputStream(fileOutput, manifest)) {
            List<Entity> entities = allEntities.retrieveAll();
            List<String> classNames = new ArrayList<>();

            for (Entity entity : entities) {
                if (!entity.isDraft() && !entity.isReadOnly()) {
                    String className = entity.getClassName();
                    classNames.add(className);

                    String[] classes = new String[]{
                            getEntityName(className), getInterfaceName(className),
                            getServiceName(className), getRepositoryName(className)
                    };

                    for (String c : classes) {
                        CtClass clazz = MotechClassPool.getDefault().get(c);

                        JarEntry entry = new JarEntry(createClassPath(c));
                        output.putNextEntry(entry);
                        output.write(clazz.toBytecode());
                        output.closeEntry();
                    }
                }
            }

            JarEntry jdoEntry = new JarEntry("META-INF/package.jdo");
            output.putNextEntry(jdoEntry);
            output.write(mdsConstructor.getCurrentMetadata().toString().getBytes());
            output.closeEntry();

            String blueprint = mergeTemplate(classNames, "/velocity/templates/blueprint-template.vm");
            String context = mergeTemplate(classNames, "/velocity/templates/mdsEntitiesContext-template.vm");

            JarEntry blueprintEntry = new JarEntry("META-INF/spring/blueprint.xml");
            output.putNextEntry(blueprintEntry);
            output.write(blueprint.getBytes());
            output.closeEntry();

            JarEntry contextEntry = new JarEntry("META-INF/motech/mdsEntitiesContext.xml");
            output.putNextEntry(contextEntry);
            output.write(context.getBytes());
            output.closeEntry();

            JarEntry commonContextEntry = new JarEntry("META-INF/motech/mdsCommonContext.xml");
            output.putNextEntry(commonContextEntry);
            output.write(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("META-INF/motech/mdsCommonContext.xml")));

            JarEntry dnproperties = new JarEntry("datanucleus.properties");
            output.putNextEntry(dnproperties);
            output.write(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("datanucleus.properties")));

            JarEntry mdsproperties = new JarEntry("motech-mds.properties");
            output.putNextEntry(mdsproperties);
            output.write(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("motech-mds.properties")));

            output.closeEntry();

            return tempFile.toFile();
        }
    }

    private String mergeTemplate(List<String> classNames, String templatePath) {
        List allClassesList = new ArrayList();

        for (String className : classNames) {
            Map map = new HashMap();
            map.put("name", className.substring(className.lastIndexOf(".") + 1));
            map.put("className", className);
            map.put("interface", getInterfaceName(className));
            map.put("service", getServiceName(className));
            map.put("repository", getRepositoryName(className));
            allClassesList.add(map);
        }

        VelocityEngine velocityEngine = new VelocityEngine();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("list", allClassesList);
        Template template;
        StringWriter writer = new StringWriter();

        try {
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

            template = velocityEngine.getTemplate(templatePath);
            template.merge(velocityContext, writer);
        } catch (Exception e) {
            LOGGER.error("An exception occured, while trying to load" + templatePath + " template and merge it with data", e);
        }

        return writer.toString();
    }

    private java.util.jar.Manifest createManifest() {
        java.util.jar.Manifest manifest = new java.util.jar.Manifest();
        Attributes attributes = manifest.getMainAttributes();

        String exports = createExportPackage(org.motechproject.mds.util.Constants.PackagesGenerated.ENTITY, org.motechproject.mds.util.Constants.PackagesGenerated.SERVICE);

        // standard attributes
        attributes.put(Name.MANIFEST_VERSION, Manifest.MANIFEST_VERSION);

        // osgi attributes
        attributes.putValue(Constants.BUNDLE_MANIFESTVERSION, Manifest.BUNDLE_MANIFESTVERSION);
        attributes.putValue(Constants.BUNDLE_NAME, createName());
        attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, createSymbolicName());
        attributes.putValue(Constants.BUNDLE_VERSION, bundleHeaders.getVersion());
        attributes.putValue(Constants.EXPORT_PACKAGE, exports);
        attributes.putValue(Constants.IMPORT_PACKAGE, "com.googlecode.flyway.core," +
                "                            javax.jdo.spi," +
                "                            net.sf.cglib.core," +
                "                            net.sf.cglib.proxy," +
                "                            net.sf.cglib.reflect," +
                "                            org.aopalliance.aop," +
                "                            org.datanucleus," +
                "                            org.datanucleus.api.jdo," +
                "                            org.datanucleus.state," +
                "                            org.datanucleus.store.rdbms.datasource.dbcp," +
                "                            org.eclipse.gemini.blueprint.config," +
                "                            org.motechproject.config.service," +
                "                            org.motechproject.osgi.web," +
                "                            org.motechproject.security.annotations," +
                "                            org.motechproject.security.model," +
                "                            org.motechproject.security.service," +
                "                            org.motechproject.server.config," +
                "                            org.motechproject.commons.sql.service," +
                "                            org.motechproject.mds.builder," +
                "                            org.motechproject.mds.repository," +
                "                            org.motechproject.mds.service," +
                "                            org.motechproject.mds.service.impl," +
                "                            org.springframework.aop," +
                "                            org.springframework.aop.aspectj.annotation," +
                "                            org.springframework.aop.framework," +
                "                            org.springframework.beans.factory.config," +
                "                            org.springframework.beans.factory.xml," +
                "                            org.springframework.context.config," +
                "                            org.springframework.context.support," +
                "                            org.springframework.orm.jdo," +
                "                            org.springframework.security.config," +
                "                            org.springframework.web.context," +
                "                            org.springframework.web.context.support," +
                "                            org.springframework.web.multipart.commons," +
                "                            org.springframework.web.servlet," +
                "                            org.springframework.web.servlet.config," +
                "                            org.springframework.web.servlet.mvc," +
                "                            org.springframework.web.servlet.support," +
                "                            org.springframework.web.servlet.view," +
                "                            *");

        return manifest;
    }

    private String createName() {
        return String.format("%s%s", bundleHeaders.getName(), Manifest.BUNDLE_NAME_SUFFIX);
    }

    private String createSymbolicName() {
        return String.format("%s%s", bundleHeaders.getSymbolicName(), Manifest.SYMBOLIC_NAME_SUFFIX);
    }

    private String bundleLocation() {
        Path path = FileSystems.getDefault().getPath(System.getenv("user.home"), "bundles", "mds-entities.jar");
        return path.toAbsolutePath().toString();
    }

    private String createExportPackage(String... packages) {
        StringBuilder builder = new StringBuilder();
        String prefix = "";

        for (String pack : packages) {
            builder.append(prefix);
            builder.append(pack);
            builder.append(";");
            builder.append(Constants.VERSION_ATTRIBUTE);
            builder.append("=");
            builder.append(bundleHeaders.getVersion());

            if (isBlank(prefix)) {
                prefix = ",";
            }
        }

        return builder.toString();
    }

    private String createClassPath(String className) {
        return JavassistHelper.toClassPath(className) + ".class";
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundleHeaders = new BundleHeaders(bundleContext);
    }

    @Autowired
    public void setMdsConstructor(MDSConstructor mdsConstructor) {
        this.mdsConstructor = mdsConstructor;
    }
}
