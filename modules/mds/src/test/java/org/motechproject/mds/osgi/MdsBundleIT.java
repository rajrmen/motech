package org.motechproject.mds.osgi;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.gemini.blueprint.test.platform.OsgiPlatform;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class MdsBundleIT extends BaseOsgiIT {
    private Logger logger = Logger.getLogger(this.getClass());

    private static final String SYSTEM_PACKAGES = "org.osgi.framework.system.packages";
    private static final String MDS_BUNDLE_NAME = "motech-dataservices";
    private static final String MDS_BUNDLE_ENTITIES_NAME = "motech-dataservices-entities";
    private static final String MDS_BUNDLE_SYMBOLIC_NAME = "org.motechproject." + MDS_BUNDLE_NAME;
    private static final String MDS_BUNDLE_ENTITIES_SYMBOLIC_NAME = "org.motechproject." + MDS_BUNDLE_ENTITIES_NAME;

    private static final String SAMPLE = "Sample";
    private static final String EXAMPLE = "Example";
    private static final String FOO = "Foo";
    private static final String BAR = "Bar";

    private EntityService entityService;
    private AllEntities allEntities;
    private JarGeneratorService jarGeneratorService;

    @Override
    public void onSetUp() throws Exception {
        WebApplicationContext context = getContext(MDS_BUNDLE_SYMBOLIC_NAME);
        entityService = (EntityService) context.getBean("entityServiceImpl");
        allEntities = (AllEntities) context.getBean("allEntities");
        jarGeneratorService = (JarGeneratorService) context.getBean("jarGeneratorServiceImpl");

        try {
            entityService.createEntity(new EntityDto(null, EXAMPLE));
            entityService.createEntity(new EntityDto(null, FOO));
            entityService.createEntity(new EntityDto(null, BAR));
        } catch (Exception e) {

        }
    }

    @Override
    public void onTearDown() throws Exception {
        try {
            entityService.deleteEntity(9997L);
            entityService.deleteEntity(9998L);
            entityService.deleteEntity(9999L);
        } catch (Exception e) {

        }
    }

    public void testEntitiesBundleInstallsProperly() throws NotFoundException, CannotCompileException, IOException, InvalidSyntaxException, InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        jarGeneratorService.regenerateMdsDataBundle();


        WebApplicationContext entitiesContext = getContext(MDS_BUNDLE_ENTITIES_SYMBOLIC_NAME);

        assertNotNull(entitiesContext);

        String beanName = "FooService";

        DefaultMotechDataService service = (DefaultMotechDataService) entitiesContext.getBean(beanName);
        String actualService = service.getClass().getSimpleName();
        actualService = actualService.substring(0, actualService.indexOf("$"));
        String expectedService = beanName.concat("Impl");
        assertEquals(actualService, expectedService);


        Class clazz = service.getClass().getClassLoader().loadClass("org.motechproject.mdsgenerated.entity.Foo");
        Object o = clazz.newInstance();

        service.create(o);

    }

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

    private WebApplicationContext getContext(String bundleName) throws InvalidSyntaxException, InterruptedException {
        WebApplicationContext theContext = null;

        int tries = 0;

        do {
            ServiceReference[] references =
                    bundleContext.getAllServiceReferences(WebApplicationContext.class.getName(), null);

            for (ServiceReference ref : references) {
                if (bundleName.equals(ref.getBundle().getSymbolicName())) {
                    theContext = (WebApplicationContext) bundleContext.getService(ref);
                    break;
                }
            }

            ++tries;
            Thread.sleep(2000);
        } while (theContext == null && tries < 5);

        assertNotNull("Unable to retrieve the bundle context", theContext);

        return theContext;
    }

    @Override
    protected List<String> getImports() {
        return asList(
               "org.motechproject.mds.repository", "org.motechproject.mds.service", "org.motechproject.mds.service.impl", "org.motechproject.commons.sql.service"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/testBundleContext.xml"};
    }
}
