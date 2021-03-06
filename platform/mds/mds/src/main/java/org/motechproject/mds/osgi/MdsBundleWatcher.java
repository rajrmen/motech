package org.motechproject.mds.osgi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.commons.api.ThreadSuspender;
import org.motechproject.mds.annotations.internal.EntityProcessorOutput;
import org.motechproject.mds.annotations.internal.MDSAnnotationProcessor;
import org.motechproject.mds.annotations.internal.MDSProcessorOutput;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.JsonLookupDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.lookup.EntityLookups;
import org.motechproject.mds.repository.SchemaChangeLockManager;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.JsonLookupService;
import org.motechproject.mds.service.MigrationService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.apache.commons.lang.StringUtils.startsWith;

/**
 * The <code>MdsBundleWatcher</code> in Motech Data Services listens for bundle installation and
 * processes the annotations in the given bundle. It also processes all installed bundles after startup.
 * After annotations are found in a bundle, the entities jar is regenerated and the target bundle is refreshed.
 */
@Component
public class MdsBundleWatcher implements SynchronousBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsBundleWatcher.class);
    private static final Gson GSON = new GsonBuilder().create();

    private static final String MDS_LOOKUPS_JSON = "mds-lookups.json";
    private static final String EMPTY_JSON = "\"" + MDS_LOOKUPS_JSON + "\" file is empty in module \"{}\".";
    private static final int MAX_WAIT_TO_RESOLVE = 10;

    private MDSAnnotationProcessor processor;
    private JarGeneratorService jarGeneratorService;
    private MigrationService migrationService;
    private BundleContext bundleContext;
    private EntitiesBundleMonitor monitor;
    private EntityService entityService;
    private JsonLookupService jsonLookupService;
    private JdoTransactionManager transactionManager;
    private List<Bundle> bundlesToRefresh;
    private SchemaChangeLockManager schemaChangeLockManager;

    private boolean processingSuspended = false;
    private Queue<AwaitingBundle> awaitingBundles = new LinkedBlockingQueue<>();

    private final Object lock = new Object();

    // called by the initializer after the initial entities bundle was generated
    public void start() {
        LOGGER.info("Scanning for MDS annotations");
        bundlesToRefresh = new ArrayList<>();

        TransactionTemplate tmpl = new TransactionTemplate(transactionManager);

        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - start annotation processing");

                processInstalledBundles();

                schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - start annotation processing");
            }
        });

        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - start refreshing bundles");

                // if we found annotations, we will refresh the bundle in order to start weaving the
                // classes it exposes
                if (!bundlesToRefresh.isEmpty()) {
                    refreshBundles(bundlesToRefresh);
                }

                schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - start refreshing bundles");
            }
        });

        bundleContext.addBundleListener(this);
    }

    /**
     * Invoked, when an event about bundle change is received. In case a new bundle gets installed
     * or an existing bundle is updated, we need to scan that bundle for MDS annotations and process them.
     *
     * @param event BundleEvent, generated by the OSGi framework
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        final Bundle bundle = event.getBundle();

        final int eventType = event.getType();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Bundle event of type {} received from {}: {} -> {}", OsgiStringUtils.nullSafeBundleEventToString(event.getType()),
                    bundle.getSymbolicName(), String.valueOf(eventType), String.valueOf(bundle.getState()));
        }

        handleBundleEvent(bundle, eventType);
    }

    private void processInstalledBundles() {
        List<MDSProcessorOutput> outputs = new ArrayList<>();

        for (Bundle bundle : bundleContext.getBundles()) {
            MDSProcessorOutput output = process(bundle);
            if (hasNonEmptyOutput(output)) {
                outputs.add(output);

                bundlesToRefresh.add(bundle);
                try {
                    migrationService.processBundle(bundle);
                } catch (IOException e) {
                    LOGGER.error("An error occurred while copying the migrations from bundle: {}", bundle.getSymbolicName(), e);
                }

                addEditableLookups(output, bundle);
            }
        }

        for (MDSProcessorOutput output : outputs) {
            processAnnotationScanningResults(output.getEntityProcessorOutputs(), output.getLookupProcessorOutputs());
        }
    }

    private void handleBundleEvent(final Bundle bundle, final int eventType) {
        if (eventType == BundleEvent.INSTALLED || eventType == BundleEvent.UPDATED) {
            if (processingSuspended) {
                awaitingBundles.add(new AwaitingBundle(bundle, eventType));
            } else {
                processBundle(bundle);
            }
        } else if (eventType == BundleEvent.UNRESOLVED && !skipBundle(bundle)) {
            LOGGER.info("Unregistering JDO classes for Bundle: {}", bundle.getSymbolicName());
            MdsBundleHelper.unregisterBundleJDOClasses(bundle);
        } else if (eventType == BundleEvent.UNINSTALLED && !skipBundle(bundle)) {
            refreshBundle(bundle);
        }
    }

    private void processBundle(final Bundle bundle) {
        final MDSProcessorOutput output = process(bundle);
        if (hasNonEmptyOutput(output)) {
            TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - saving output of bundle processing");

                    processAnnotationScanningResults(output.getEntityProcessorOutputs(), output.getLookupProcessorOutputs());

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - saving output of bundle processing");
                }
            });

            tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - searching for flyway migrations");

                    try {
                        migrationService.processBundle(bundle);
                    } catch (IOException e) {
                        LOGGER.error("An error occurred while copying the migrations from bundle: {}", bundle.getSymbolicName(), e);
                    }

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - searching for flyway migrations");
                }
            });
            // if we found annotations, we will refresh the bundle in order to start weaving the
            // classes it exposes
            tmpl = new TransactionTemplate(transactionManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    schemaChangeLockManager.acquireLock(MdsBundleWatcher.class.getName() + " - refreshing after bundle event");

                    refreshBundle(bundle);

                    schemaChangeLockManager.releaseLock(MdsBundleWatcher.class.getName() + " - refreshing after bundle event");
                }
            });
        }
    }

    private MDSProcessorOutput process(Bundle bundle) {
        if (skipBundle(bundle)) {
            return null;
        }

        synchronized (lock) {
            // Before we process annotations, we wait until bundle resolves its dependencies
            int count = 0;
            while (bundle.getState() < Bundle.RESOLVED && count < MAX_WAIT_TO_RESOLVE) {
                ThreadSuspender.sleep(500);
                count++;
            }

            LOGGER.debug("Processing bundle {}", bundle.getSymbolicName());
            return processor.processAnnotations(bundle);
        }
    }

    private boolean skipBundle(Bundle bundle) {
        // we skip the generated entities bundle, MDS bundle and the framework bundle
        if (MdsBundleHelper.isMdsBundle(bundle) || MdsBundleHelper.isMdsEntitiesBundle(bundle) ||
                MdsBundleHelper.isFrameworkBundle(bundle)) {
            return true;
        }

        // we also skip bundles which locations start with "link:", as these are pax exam bundles, which we
        // encounter only during tests. Maybe in some distant future, support for resolving these locations will be
        // added, but there is no need to do it right now.
        if (startsWith(bundle.getLocation(), "link:") || startsWith(bundle.getLocation(), "local")) {
            return true;
        }

        // finally we skip bundles that don't have an MDS dependency
        return !MdsBundleHelper.isBundleMdsDependent(bundle);
    }

    private void refreshBundle(Bundle bundle) {
        refreshBundles(Arrays.asList(bundle));
    }

    private void refreshBundles(List<Bundle> bundles) {
        if (LOGGER.isInfoEnabled()) {
            for (Bundle bundle : bundles) {
                LOGGER.info("Refreshing wiring for bundle {}", bundle.getSymbolicName());
            }
        }

        // we generate the entities bundle but not start it to avoid exceptions when the framework
        // will refresh bundles
        jarGeneratorService.regenerateMdsDataBundle(false);

        FrameworkWiring framework = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
        framework.refreshBundles(bundles);

        // give the framework 3 seconds to do a refresh
        ThreadSuspender.sleep(3000);

        // after refreshing all bundles we can start the entities bundle
        monitor.start();
    }

    private void processAnnotationScanningResults(List<EntityProcessorOutput> entityProcessorOutput, Map<String, List<LookupDto>> lookupProcessingResult) {
        Map<String, Long> entityIdMappings = new HashMap<>();

        for (EntityProcessorOutput result : entityProcessorOutput) {
            EntityDto processedEntity = result.getEntityProcessingResult();

            EntityDto entity = entityService.getEntityByClassName(processedEntity.getClassName());

            if (entity == null) {
                entity = entityService.createEntity(processedEntity);
            }
            entityIdMappings.put(entity.getClassName(), entity.getId());

            entityService.updateRestOptions(entity.getId(), result.getRestProcessingResult());
            entityService.updateTracking(entity.getId(), result.getTrackingProcessingResult());
            entityService.addFields(entity, result.getFieldProcessingResult());
            entityService.addFilterableFields(entity, result.getUiFilterableProcessingResult());
            entityService.addDisplayedFields(entity, result.getUiDisplayableProcessingResult());
            entityService.updateSecurityOptions(entity.getId(), processedEntity.getSecurityMode(),
                    processedEntity.getSecurityMembers());
            entityService.updateMaxFetchDepth(entity.getId(), processedEntity.getMaxFetchDepth());
            entityService.addNonEditableFields(entity, result.getNonEditableProcessingResult());
        }

        for (Map.Entry<String, List<LookupDto>> entry : lookupProcessingResult.entrySet()) {
            entityService.addLookups(entityIdMappings.get(entry.getKey()), entry.getValue());
        }
    }

    private void addEditableLookups(MDSProcessorOutput output, Bundle bundle) {

        URL lookupsResource = bundle.getResource(MDS_LOOKUPS_JSON);

        if (lookupsResource != null) {

            try (InputStream stream = lookupsResource.openStream()) {

                String lookupsJson = IOUtils.toString(stream);

                if (StringUtils.isNotBlank(lookupsJson)) {

                    EntityLookups[] entitiesLookups = GSON.fromJson(lookupsJson, EntityLookups[].class);

                    for (EntityLookups entityLookups : entitiesLookups) {
                        addEditableEntityLookups(output, entityLookups);
                    }
                } else {
                    LOGGER.info(EMPTY_JSON, bundle.getSymbolicName());
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void addEditableEntityLookups(MDSProcessorOutput output, EntityLookups entityLookups) {

        String entityClassName = entityLookups.getEntityClassName();
        List<LookupDto> lookups = getLookups(output, entityClassName);

        if (lookups == null) {
            lookups = new ArrayList<>();
            output.getLookupProcessorOutputs().put(entityClassName, lookups);
        }

        for (LookupDto lookup : entityLookups.getLookups()) {
            if (!jsonLookupService.exists(entityClassName, lookup.getLookupName())) {

                lookups.add(lookup);

                JsonLookupDto jsonLookup = new JsonLookupDto(entityClassName, lookup.getLookupName());
                jsonLookupService.createJsonLookup(jsonLookup);

                LOGGER.debug("Added \"{}\" lookup for \"{}\" entity", lookup.getLookupName(), entityClassName);
            }
        }
    }

    private List<LookupDto> getLookups(MDSProcessorOutput output, String entityClassName) {
        return output.getLookupProcessorOutputs().get(entityClassName);
    }

    private boolean hasNonEmptyOutput(MDSProcessorOutput output) {
        return output != null && !(output.getEntityProcessorOutputs().isEmpty() &&
                output.getLookupProcessorOutputs().isEmpty());
    }

    public void suspendProcessing() {
        processingSuspended = true;
    }

    public void restoreProcessing() {
        processingSuspended = false;

        while (!awaitingBundles.isEmpty()) {
            AwaitingBundle awaitingBundle = awaitingBundles.poll();
            processBundle(awaitingBundle.bundle);
        }
    }

    @Autowired
    public void setProcessor(MDSAnnotationProcessor processor) {
        this.processor = processor;
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }

    @Autowired
    public void setMigrationService(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setMonitor(EntitiesBundleMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setSchemaChangeLockManager(SchemaChangeLockManager schemaChangeLockManager) {
        this.schemaChangeLockManager = schemaChangeLockManager;
    }

    @Autowired
    public void setJsonLookupService(JsonLookupService jsonLookupService) {
        this.jsonLookupService = jsonLookupService;
    }

    private class AwaitingBundle {
        private Bundle bundle;
        private int eventType;

        public AwaitingBundle(Bundle bundle, int eventType) {
            this.bundle = bundle;
            this.eventType = eventType;
        }
    }
}
