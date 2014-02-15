package org.motechproject.mds.service.impl.internal;

import javassist.CtClass;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.MDSConstructor;
import org.motechproject.mds.util.Constants;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.metadata.JDOMetadata;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl extends BaseMdsService implements MDSConstructor {

    private SettingsFacade settingsFacade;
    private AllEntities allEntities;
    private EntityBuilder entityBuilder;
    private EntityInfrastructureBuilder infrastructureBuilder;
    private EntityMetadataBuilder metadataBuilder;

    private JDOMetadata jdoMetadata;

    @Override
    public JDOMetadata getCurrentMetadata() {
        if (jdoMetadata == null) {
            jdoMetadata = getPersistenceManagerFactory().newMetadata();
        }
        return jdoMetadata;
    }

    @Override
    @Transactional
    public void constructEntity(Entity entity) {
        CtClass existingClass = MotechClassPool.getDefault().getOrNull(entity.getClassName());

        if (existingClass == null) {
            // just add a class
            constructSingleEntity(entity);
        } else {
            // editing a class requires reloading the classLoader and regenerating the entities
            MDSClassLoader.reloadClassLoader();
            generateAllEntities();
        }
    }

    @Override
    public void generateAllEntities() {
        // we need an jdo enhancer and a temporary classloader
        // to define classes in before enhancement
        MDSClassLoader tmpClassLoader = new MDSClassLoader();
        MdsJDOEnhancer enhancer = createEnhancer(tmpClassLoader);

        // process only entities that are not drafts
        List<Entity> entities = allEntities.retrieveAll();
        filterEntities(entities);

        // generate jdo metadata for our entities
        jdoMetadata = getPersistenceManagerFactory().newMetadata();
        for (Entity entity : entities) {
            metadataBuilder.addEntityMetadata(jdoMetadata, entity);
        }

        // next we create the java classes and add them to both
        // the temporary classloader and enhancer
        for (Entity entity : entities) {
            ClassData classData = entityBuilder.build(entity);

            tmpClassLoader.defineClass(classData);
            enhancer.addClass(classData);
        }

        // after the classes are defined, we register their metadata
        enhancer.registerMetadata(jdoMetadata);

        // then, we commence with enhancement
        enhancer.enhance();

        // lastly, we define the enhanced class in the main classloader
        // and build the infrastructure classes
        for (Entity entity : entities) {
            String className = entity.getClassName();
            byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

            MDSClassLoader.getInstance().defineClass(className, enhancedBytes);

            infrastructureBuilder.buildInfrastructure(className);
        }
    }

    private void constructSingleEntity(Entity entity) {
        // we need an jdo enhancer and a temporary classloader
        // to define classes in before enhancement
        MDSClassLoader tmpClassLoader = new MDSClassLoader();
        MdsJDOEnhancer enhancer = createEnhancer(tmpClassLoader);

        metadataBuilder.addEntityMetadata(jdoMetadata, entity);

        ClassData classData = entityBuilder.build(entity);

        tmpClassLoader.defineClass(classData);
        enhancer.addClass(classData);

        // after the classes are defined, we register their metadata
        enhancer.registerMetadata(jdoMetadata);

        // then, we commence with enhancement
        enhancer.enhance();

        // lastly, we define the enhanced class in the main classloader
        // and build the infrastructure classes
        String className = entity.getClassName();
        byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

        //Class<?> clazz = MDSClassLoader.getInstance().defineClass(className, enhancedBytes);

        infrastructureBuilder.buildInfrastructure(className);
    }

    private void filterEntities(List<Entity> entities) {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity entity = it .next();
            if (entity.isDraft() || entity.isReadOnly()) {
                it.remove();
            }
        }
    }

    private MdsJDOEnhancer createEnhancer(ClassLoader enhancerClassLoader) {
        Properties config = settingsFacade.getProperties(Constants.Config.DATANUCLEUS_FILE);
        return new MdsJDOEnhancer(config, enhancerClassLoader);
    }

    @Autowired
    public void setEntityBuilder(EntityBuilder entityBuilder) {
        this.entityBuilder = entityBuilder;
    }

    @Autowired
    public void setInfrastructureBuilder(EntityInfrastructureBuilder infrastructureBuilder) {
        this.infrastructureBuilder = infrastructureBuilder;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setMetadataBuilder(EntityMetadataBuilder metadataBuilder) {
        this.metadataBuilder = metadataBuilder;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }
}
