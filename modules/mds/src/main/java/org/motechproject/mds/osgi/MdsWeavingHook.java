package org.motechproject.mds.osgi;

import org.motechproject.mds.builder.EnhancedClassData;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MdsWeavingHook implements WeavingHook {

    private static final Logger LOG = LoggerFactory.getLogger(MdsWeavingHook.class);

    @PostConstruct
    public void init() {
        LOG.info("MDS weaving hook initialized");
    }

    @Override
    public void weave(WovenClass wovenClass) {
        String className = wovenClass.getClassName();

        if (className.contains("MdsExample")) {
            LOG.info("WINNER WINNER CHICKEN DINNER");
        }

        LOG.trace("Weaving called for: {}", className);

        String realDDEClassName = ClassName.getDDEName(className);

        EnhancedClassData enhancedClassData = MotechClassPool.getEnhancedData(realDDEClassName);

        if (enhancedClassData == null) {
            LOG.trace("{} does not have enhanced registered DDE metadata", className);
        } else {
            LOG.debug("Weaving {}", className);
            wovenClass.setBytes(enhancedClassData.getBytecode());
        }
    }
}
