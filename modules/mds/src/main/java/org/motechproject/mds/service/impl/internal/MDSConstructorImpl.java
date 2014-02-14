package org.motechproject.mds.service.impl.internal;

import javassist.CtClass;
import org.apache.commons.collections.CollectionUtils;
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
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.metadata.JDOMetadata;
import java.util.Iterator;
import java.util.List;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl extends BaseMdsService implements MDSConstructor {

    private SettingsFacade setttingsFacade;
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

/*            constructEntity(entity, new MDSClassLoader());


            ClassData classData = enhancer.executeEnhancement(entity, getCurrentMetadata());
            postEnhancement(classData);*/
        } else {
            // editing a class requires reloading the classLoader and regenerating the entities
            MDSClassLoader.reloadClassLoader();
            generateAllEntities();
        }
    }

    private void postEnhancement(ClassData enhancedClassData) {
        Class<?> clazz = MDSClassLoader.getInstance().defineClass(enhancedClassData);
        buildInfrastructure(clazz);
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

    @Override
    public void generateAllEntities() {
        MDSClassLoader tmpClassLoader = new MDSClassLoader();
        MdsJDOEnhancer enhancer = new MdsJDOEnhancer(setttingsFacade, tmpClassLoader);

        List<Entity> entities = allEntities.retrieveAll();
        filterEntities(entities);

        jdoMetadata = getPersistenceManagerFactory().newMetadata();
        for (Entity entity : entities) {
            metadataBuilder.addEntityMetadata(jdoMetadata, entity);
        }

        for (Entity entity : entities) {
            ClassData classData = entityBuilder.build(entity);
            tmpClassLoader.defineClass(classData);
            enhancer.addClass(classData.getClassName(), classData.getBytecode());
        }

        enhancer.registerMetadata(jdoMetadata);

        enhancer.enhance();

        for (Entity entity : entities) {
            String className = entity.getClassName();
            byte[] enhancedBytes = enhancer.getEnhancedBytes(className);

            Class<?> clazz = MDSClassLoader.getInstance().defineClass(className, enhancedBytes);

            buildInfrastructure(clazz);
        }
    }

    private void buildInfrastructure(Class<?> clazz) {
        List<ClassData> classes = infrastructureBuilder.buildInfrastructure(clazz);

        if (CollectionUtils.isNotEmpty(classes)) {
            for (ClassData classData : classes) {
                MDSClassLoader.getInstance().defineClass(classData.getClassName(), classData.getBytecode());
            }
        }
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
    public void setSetttingsFacade(SettingsFacade setttingsFacade) {
        this.setttingsFacade = setttingsFacade;
    }
}
