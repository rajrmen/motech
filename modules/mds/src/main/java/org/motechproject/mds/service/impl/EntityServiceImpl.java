package org.motechproject.mds.service.impl;

import org.apache.commons.lang.WordUtils;
import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;
import java.io.IOException;
import java.util.Properties;

/**
 * Default implmenetation of {@link org.motechproject.mds.service.EntityService} interface.
 */
@Service
public class EntityServiceImpl extends BaseMdsService implements EntityService {
    private static final String DATANUCLEUS_PROPERTIES = "datanucleus.properties";

    @Override
    @Transactional
    public EntityDto createEntity(EntityDto entity) {
        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        try {
            EntityBuilder builder = new EntityBuilder()
                    .withSingleName(entity.getName())
                    .withClassLoader(getEnhancerClassLoader())
                    .build();

            String className = builder.getClassName();
            byte[] enhancedBytes = enhance(builder);

            getPersistenceClassLoader().defineClass(className, enhancedBytes);
            JDOMetadata metadata = populate(getPersistenceManagerFactory().newMetadata(), className);

            getPersistenceManagerFactory().registerMetadata(metadata);

            Class clazz = getPersistenceClassLoader().loadClass(className);
            Object obj = clazz.newInstance();

            getPersistenceManager().makePersistent(obj);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new MdsException(e.getMessage());
        }

        return entity;
    }

    private byte[] enhance(EntityBuilder builder) throws IOException {
        Properties properties = getSettingsFacade().getProperties(DATANUCLEUS_PROPERTIES);
        JDOEnhancer enhancer = new JDOEnhancer(properties);
        enhancer.setVerbose(true);
        enhancer.setClassLoader(builder.getClassLoader());

        JDOMetadata metadata = populate(enhancer.newMetadata(), builder.getClassName());
        enhancer.registerMetadata(metadata);
        enhancer.addClass(builder.getClassName(), builder.getClassBytes());
        enhancer.enhance();

        return enhancer.getEnhancedBytes(builder.getClassName());
    }

    private JDOMetadata populate(JDOMetadata md, String className) {
        String packageName = className.substring(0, className.lastIndexOf("."));
        String simpleName = className.substring(className.lastIndexOf(".") + 1);

        PackageMetadata pmd = md.newPackageMetadata(packageName);
        ClassMetadata cmd = pmd.newClassMetadata(simpleName);

        cmd.setTable(WordUtils.capitalize(simpleName));
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        return md;
    }
}
