package org.motechproject.mds.osgi;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.gemini.blueprint.test.platform.OsgiPlatform;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MdsBundleIT extends BaseOsgiIT {
    private Logger logger = Logger.getLogger(this.getClass());

    private static final String SYSTEM_PACKAGES = "org.osgi.framework.system.packages";

    //private static final String SAMPLE_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, SAMPLE);
    //private static final String EXAMPLE_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, EXAMPLE);
    //private static final String FOO_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, FOO);
    //private static final String BAR_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, BAR);


    @Override
    protected String[] getTestBundlesNames() {
        String[] names = super.getTestBundlesNames();

        int paranamerSourcesIndex = -1;

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (StringUtils.equals(name, "com.thoughtworks.paranamer,paranamer,sources")) {
                paranamerSourcesIndex = i;
                break;
            }
        }

        if (paranamerSourcesIndex >= 0) {
            names = (String[]) ArrayUtils.remove(names, paranamerSourcesIndex);
        }

        return names;
    }

    public void testSomething() {
    }

    @Override
    protected OsgiPlatform createPlatform() {
        OsgiPlatform platform = super.createPlatform();

        try (InputStream in = getClass().getResourceAsStream("/osgi.properties")) {
            Properties osgiProperties = new Properties();
            osgiProperties.load(in);

            platform.getConfigurationProperties().setProperty(SYSTEM_PACKAGES, osgiProperties.getProperty(SYSTEM_PACKAGES));
        } catch (IOException e) {
            logger.error("Cannot read osgi.properties", e);
        }

        return platform;
    }

    public void setUpp() throws Exception {
        //entityService.createEntity(new EntityDto(null, SAMPLE));
        //entityService.createEntity(new EntityDto(null, EXAMPLE));
        //entityService.createEntity(new EntityDto(null, FOO));
        //entityService.createEntity(new EntityDto(null, BAR));
    }


    public void tearDownn() throws Exception {
        //getPersistenceManager().newQuery(EntityMapping.class).deletePersistentAll();
    }

    //public void testMdsEntitiesBundle() {
        //File tempFile = jarGeneratorService.generate();
        //Bundle dataBundle;

       // Bundle[] bundles = bundle.getBundleContext().getBundles();


        //try (InputStream in = new FileInputStream(tempFile)) {

        //    dataBundle = bundleContext.installBundle(tempFile.getPath(), in);
            //dataBundle.start();

            //Bundle[] bundles = bundleContext.getBundles();

            //ServiceReference[] serviceReferences = dataBundle.getRegisteredServices();
            //String s = "s";


        //}
    //}

}
