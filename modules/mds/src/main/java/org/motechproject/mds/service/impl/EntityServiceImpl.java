package org.motechproject.mds.service.impl;

import org.apache.commons.lang.WordUtils;
import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Transactional;
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
    private PersistenceManagerFactory persistenceManagerFactory;
    private Properties dataNucleusProperties;

    @Autowired
    public EntityServiceImpl(@Qualifier("persistenceManagerFactory") PersistenceManagerFactory pmf,
                             SettingsFacade settingsFacade) {
        this.persistenceManagerFactory = pmf;
        this.dataNucleusProperties = settingsFacade.getProperties("datanucleus.properties");
    }

    @Override
    @Transactional
    public EntityDto createEntity(EntityDto entity) {
        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        try {
            EntityBuilder builder = new EntityBuilder()
                    .withSingleName(entity.getName())
                    .withClassLoader(getEnhancerClassLoader());

            byte[] enhancedBytes = enhance(builder, getEnhancerClassLoader());
            getPersistenceClassLoader().defineClass(builder.getClassName(), enhancedBytes);
            JDOMetadata metadata = populate(persistenceManagerFactory.newMetadata(), builder.getClassName());

            persistenceManagerFactory.registerMetadata(metadata);
        } catch (IOException e) {
            throw new MdsException(e.getMessage());
        }

        return entity;
    }

    private byte[] enhance(EntityBuilder builder, ClassLoader classLoader) throws IOException {
        JDOEnhancer enhancer = new JDOEnhancer(dataNucleusProperties);
        enhancer.setVerbose(true);

        enhancer.setClassLoader(classLoader);
        enhancer.registerMetadata(populate(enhancer.newMetadata(), builder.getClassName()));
        enhancer.addClass(builder.getClassName(), builder.build());
        enhancer.enhance();

        return enhancer.getEnhancedBytes(builder.getClassName());
    }

    private JDOMetadata populate(JDOMetadata md, String className) {
        String packageName = className.substring(0, className.lastIndexOf("."));
        PackageMetadata pmd = md.newPackageMetadata(packageName);
        String simpleName = className.substring(className.lastIndexOf(".") + 1);
        ClassMetadata cmd = pmd.newClassMetadata(simpleName);

        cmd.setTable(WordUtils.capitalize(simpleName)).setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        return md;
    }
}
