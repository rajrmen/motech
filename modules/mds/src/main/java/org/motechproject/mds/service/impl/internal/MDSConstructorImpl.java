package org.motechproject.mds.service.impl.internal;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EnhancedClassData;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.MDSConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;
import java.util.List;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.MDSConstructor} interface.
 */
@Service
public class MDSConstructorImpl extends BaseMdsService implements MDSConstructor {
    private MdsJDOEnhancer enhancer;

    private EntityBuilder entityBuilder;
    private EntityInfrastructureBuilder infrastructureBuilder;
    private EntityMetadataBuilder metadataBuilder;

    @Override
    @Transactional
    public void constructEntity(EntityMapping mapping) {
        try {
            ClassData classData = entityBuilder.build(mapping);

            MDSClassLoader tmpClassLoader = new MDSClassLoader();
            tmpClassLoader.defineClass(classData);

            EnhancedClassData enhancedClassData = enhancer.enhance(mapping, classData.getBytecode(), tmpClassLoader);

            Class<?> clazz = MDSClassLoader.getInstance().defineClass(enhancedClassData);

            JDOMetadata jdoMetadata = metadataBuilder.createBaseEntity(
                    getPersistenceManagerFactory().newMetadata(), mapping);

            getPersistenceManagerFactory().registerMetadata(jdoMetadata);

            buildInfrastructure(clazz);
        } catch (IOException e) {
            throw new EntityCreationException(e);
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
    public void setEnhancer(MdsJDOEnhancer enhancer) {
        this.enhancer = enhancer;
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
    public void setMetadataBuilder(EntityMetadataBuilder metadataBuilder) {
        this.metadataBuilder = metadataBuilder;
    }
}
