package org.motechproject.mds.osgi;

import org.apache.log4j.Logger;
import org.eclipse.gemini.blueprint.test.platform.EquinoxPlatform;
import org.eclipse.gemini.blueprint.test.platform.OsgiPlatform;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Constants;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Properties;

public class MdsBundleIT extends BaseOsgiIT {
    private Logger logger = Logger.getLogger(this.getClass());

    //private static final String SAMPLE_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, SAMPLE);
    //private static final String EXAMPLE_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, EXAMPLE);
    //private static final String FOO_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, FOO);
    //private static final String BAR_CLASS = String.format("%s.%s", Constants.Packages.ENTITY, BAR);


    @Override
    protected OsgiPlatform createPlatform() {
        boolean trace = logger.isTraceEnabled();
        String platformClassName = getPlatformName();

        OsgiPlatform platform = null;
        ClassLoader currentCL = getClass().getClassLoader();

        if (StringUtils.hasText(platformClassName)) {
            if (ClassUtils.isPresent(platformClassName, currentCL)) {
                Class<?> platformClass = ClassUtils.resolveClassName(platformClassName, currentCL);
                if (OsgiPlatform.class.isAssignableFrom(platformClass)) {
                    if (trace)
                        logger.trace("Instantiating platform wrapper...");
                    try {
                        platform = (OsgiPlatform) platformClass.newInstance();
                    }
                    catch (Exception ex) {
                        logger.warn("cannot instantiate class [" + platformClass + "]; using default");
                    }
                }
                else
                    logger.warn("Class [" + platformClass + "] does not implement " + OsgiPlatform.class.getName()
                            + " interface; falling back to defaults");
            }
            else {
                logger.warn("OSGi platform starter [" + platformClassName + "] not found; using default");
            }

        }
        else
            logger.trace("No platform specified; using default");

        // fall back
        if (platform == null)
            platform = new EquinoxPlatform();

        Properties config = platform.getConfigurationProperties();
        // add boot delegation
        config.setProperty(Constants.FRAMEWORK_BOOTDELEGATION,
                getBootDelegationPackageString());

        return platform;
    }

    private String getBootDelegationPackageString() {
        StringBuilder buf = new StringBuilder();

        for (Iterator iter = getBootDelegationPackages().iterator(); iter.hasNext();) {
            String s = (String) iter.next();
            buf.append(s.trim());
            if (iter.hasNext()) {
                buf.append(",");
            }
        }
        return buf.toString();
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
