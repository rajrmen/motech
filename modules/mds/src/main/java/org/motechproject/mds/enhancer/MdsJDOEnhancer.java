package org.motechproject.mds.enhancer;

import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;

import static org.motechproject.mds.constants.Constants.Config;

/**
 * The <code>MdsJDOEnhancer</code> class is a wrapper for
 * {@link org.datanucleus.api.jdo.JDOEnhancer} class. Its task is to add the missing information
 * into created entity class.
 */
@Component
public class MdsJDOEnhancer extends JDOEnhancer {

    @Autowired
    private EntityBuilder entityBuilder;

    @Autowired
    public MdsJDOEnhancer(SettingsFacade settingsFacade) {
        super(settingsFacade.getProperties(Config.DATANUCLEUS_FILE));

        setVerbose(true);
    }

    public byte[] enhance(EntityMapping mapping) throws IOException {
        byte[] classBytes = entityBuilder.build(mapping);

        setClassLoader(MDSClassLoader.PERSISTENCE);

        JDOMetadata metadata = EntityMetadataBuilder.createBaseEntity(newMetadata(), mapping);

        registerMetadata(metadata);
        addClass(mapping.getClassName(), classBytes);
        enhance();

        return getEnhancedBytes(mapping.getClassName());
    }
}
