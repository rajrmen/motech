package org.motechproject.mds.osgi;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.gemini.blueprint.test.platform.OsgiPlatform;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.NameConventions;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.DesignDocumentFactory;
import org.ektorp.util.Assert;
import org.ektorp.util.Documents;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static java.util.Arrays.asList;

public class MdsPerformanceIT extends BaseOsgiIT {
    private static final Logger logger = LoggerFactory.getLogger(MdsPerformanceIT.class);

    private static final String SYSTEM_PACKAGES = "org.osgi.framework.system.packages";
    private static final String MDS_BUNDLE_NAME = "motech-dataservices";
    private static final String MDS_BUNDLE_ENTITIES_NAME = "motech-dataservices-entities";
    private static final String MDS_BUNDLE_SYMBOLIC_NAME = "org.motechproject." + MDS_BUNDLE_NAME;
    private static final String MDS_BUNDLE_ENTITIES_SYMBOLIC_NAME = "org.motechproject." + MDS_BUNDLE_ENTITIES_NAME;

    private static final String FOO = "Foo";
    private static final String FOO_CLASS = String.format("%s.%s", Constants.PackagesGenerated.ENTITY, FOO);

    private static final int TEST_INSTANCES = 10;

    private EntityService entityService;
    private AllEntities allEntities;
    private CouchMdsRepository couchMdsRepository;
    //private PersistenceManagerFactory persistenceManagerFactory;


    @Override
    public void onSetUp() throws Exception {
        WebApplicationContext context = getContext(MDS_BUNDLE_SYMBOLIC_NAME);
        entityService = (EntityService) context.getBean("entityServiceImpl");
        allEntities = (AllEntities) context.getBean("allEntities");
        //persistenceManagerFactory = (PersistenceManagerFactory) context.getBean("persistenceManagerFactory");

        clearEntities();
        setUpSecurityContext();
    }

    @Override
    public void onTearDown() throws Exception {
        clearEntities();
    }

    @Transactional
    public void testPerformance() throws NotFoundException, CannotCompileException, IOException, InvalidSyntaxException, InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final String serviceName = ClassName.getInterfaceName(FOO_CLASS);

        prepareTestEntity();

        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, MDS_BUNDLE_ENTITIES_SYMBOLIC_NAME);
        assertNotNull(entitiesBundle);

        MotechDataService service = (MotechDataService) getService(serviceName);

        Class<?> objectClass = entitiesBundle.loadClass(FOO_CLASS);
        logger.info("Loaded class: " + objectClass.getName());

        StdCouchDbConnector couchDbConnector = (StdCouchDbConnector) getApplicationContext().getBean("testMdsDbConnector");
        couchMdsRepository = new CouchMdsRepository(objectClass, couchDbConnector);

        DefaultMotechDataService defaultMotechDataService = new CouchMdsService();
        defaultMotechDataService.setRepository(couchMdsRepository);
        defaultMotechDataService.setAllEntities(allEntities);

        compareCreating(service, objectClass, defaultMotechDataService);
        compareRetrieval(service);
        compareDeleting(service);
    }

    @Transactional
    private void compareCreating(MotechDataService service, Class clazz, DefaultMotechDataService couchMdsService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Object> instances = prepareInstances(clazz);

        Long startTime = System.nanoTime();

        for (Object instance : instances) {
            service.create(instance);
        }

        Long endTime = (System.nanoTime() - startTime) / 10000000;

        logger.info("MDS Service: Creating " + TEST_INSTANCES + " instances took " + endTime + "ms.");

        startTime = System.nanoTime();

        for (Object instance : instances) {
            couchMdsService.create(instance);
        }

        endTime = (System.nanoTime() - startTime) / 10000000;
        logger.info("CouchDB Service: Creating " + TEST_INSTANCES + " instances took " + endTime + "ms.");
    }

    private void compareRetrieval(MotechDataService service) {
        Long startTime = System.nanoTime();

        service.retrieveAll();

        Long endTime = (System.nanoTime() - startTime) / 10000000;

        logger.info("MDS Service: Retrieving all instances took " + endTime + "ms.");
    }

    private void compareDeleting(MotechDataService service) {

    }

    private void verifyInstanceUpdating(MotechDataService<Object> service) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<Object> allObjects = service.retrieveAll();
        assertEquals(allObjects.size(), 2);

        Object retrieved = allObjects.get(0);

        MethodUtils.invokeMethod(retrieved, "setSomeString", "anotherString");
        MethodUtils.invokeMethod(retrieved, "setSomeBoolean", false);
        MethodUtils.invokeMethod(retrieved, "setSomeList", Arrays.asList(4, 5));

        service.update(retrieved);
        Object updated = service.retrieveAll().get(0);

        assertEquals(MethodUtils.invokeMethod(updated, "getSomeString", null), "anotherString");
        assertEquals(MethodUtils.invokeMethod(updated, "getSomeBoolean", null), false);
        assertEquals(MethodUtils.invokeMethod(updated, "getSomeList", null), Arrays.asList(4, 5));
    }

    private void verifyInstanceDeleting(MotechDataService<Object> service) throws IllegalAccessException, InstantiationException {
        List<Object> objects = service.retrieveAll();
        assertEquals(objects.size(), 2);

        service.delete(objects.get(0));
        assertEquals(service.retrieveAll().size(), 1);

        service.delete(objects.get(1));
        assertTrue(service.retrieveAll().isEmpty());
    }

    private List<Object> prepareInstances(Class<?> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        List<Object> instances = new ArrayList<>();
        Integer someInt = -TEST_INSTANCES/2;
        String someString = "";
        Random random = new Random(System.currentTimeMillis());


        for (int i=0; i<TEST_INSTANCES; i++) {
            Object instance = clazz.newInstance();
            MethodUtils.invokeMethod(instance, "setSomeString", someString);
            MethodUtils.invokeMethod(instance, "setSomeInt", someInt);
            instances.add(instance);
            someInt++;
            int chars = 1 + random.nextInt(253);
            someString = RandomStringUtils.random(chars);
        }

        return instances;
    }

    private void prepareTestEntity() throws IOException {
        EntityDto entityDto = new EntityDto(9999L, FOO);
        entityDto = entityService.createEntity(entityDto);

        List<FieldDto> fields = new ArrayList<>();
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.INTEGER,
                new FieldBasicDto("someInt", "someInt"),
                false, null));
        fields.add(new FieldDto(null, entityDto.getId(),
                TypeDto.STRING,
                new FieldBasicDto("someString", "someString"),
                false, null));

        entityService.addFields(entityDto, fields);
        entityService.commitChanges(entityDto.getId());
    }

    private void setUpSecurityContext() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        authentication.setAuthenticated(false);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Override
    protected String[] getTestBundlesNames() {
        // Paranamer-sources is not parsed properly by the base class, so we remove it from our dependencies
        // Apache Felix Framework seem to be duplicated somewhere what causes exception, so we remove additional one
        String[] names = super.getTestBundlesNames();
        String[] toRemove = { "com.thoughtworks.paranamer,paranamer,sources",
                "org.apache.felix,org.apache.felix.framework,3.2.0" };

        return removeTestBundles(names, toRemove);
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

    private String[] removeTestBundles(String[] initialArray, String[] toRemove) {
        for (String bundle : toRemove) {
            initialArray = (String[]) ArrayUtils.removeElement(initialArray, bundle);
        }
        return initialArray;
    }

    private void clearEntities() {
        for (EntityDto entity : entityService.listEntities()) {
            entityService.deleteEntity(entity.getId());
        }

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

    private Object getService(String className) throws InterruptedException {
        Object service = null;

        int tries = 0;

        do {
            ServiceReference ref = bundleContext.getServiceReference(className);

            if (ref != null) {
                service = bundleContext.getService(ref);
                break;
            }

            ++tries;
            Thread.sleep(5000);
        } while (tries < 5);

        assertNotNull("Unable to retrieve the service " + className, service);

        return service;
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.commons.sql.service",
                "org.motechproject.server.config",
                "org.datanucleus",
                "org.datanucleus.state",
                "org.datanucleus.api.jdo",
                "org.datanucleus.store.rdbms.datasource.dbcp",
                "com.googlecode.flyway.core",
                "org.springframework.orm.jdo",
                "org.motechproject.commons.couchdb.service",
                "org.motechproject.mds.builder",
                "org.motechproject.mds.domain",
                "org.motechproject.mds.repository",
                "org.motechproject.mds.service.impl",
                "org.motechproject.mds.service",
                "org.motechproject.mds.util"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{ "testMdsAndCouchContext.xml" };
    }

    private class CouchMdsService extends DefaultMotechDataService {

        @Override
        @Transactional
        public Object create(Object object) {
            return super.create(object);
        }

    }

    private class CouchMdsRepository<T> extends MotechDataRepository {

        protected final CouchDbConnector db;
        protected final Class<T> type;

        protected final String stdDesignDocumentId;

        private DesignDocumentFactory designDocumentFactory;

        protected CouchMdsRepository(Class<T> classType, CouchDbConnector couchDbConnector) {
            super(classType);
            Assert.notNull(couchDbConnector, "CouchDbConnector may not be null");
            Assert.notNull(classType);
            this.db = couchDbConnector;
            this.type = classType;
            db.createDatabaseIfNotExists();

            stdDesignDocumentId = NameConventions.designDocName(type);
        }

        public void add(T entity) {
            assertEntityNotNull(entity);
            Assert.isTrue(Documents.isNew(entity), "entity must be new");
            db.create(entity);
        }

        private void assertEntityNotNull(T entity) {
            Assert.notNull(entity, "entity may not be null");
        }

    }

}
