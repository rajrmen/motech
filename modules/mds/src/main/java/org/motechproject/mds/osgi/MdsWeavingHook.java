package org.motechproject.mds.osgi;

import javassist.ClassPool;
import org.motechproject.mds.builder.EnhancedClassData;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.ClassName;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MdsWeavingHook implements WeavingHook {

    private static final Logger LOG = LoggerFactory.getLogger(MdsWeavingHook.class);

    private final ClassPool classPool = MotechClassPool.getDefault();

    private EntityService entityService;
    private MdsJDOEnhancer mdsJDOEnhancer;

    @Override
    public void weave(WovenClass wovenClass) {
        String className = wovenClass.getClassName();

        LOG.trace("Attempting to weave class for: {}", className);

        String realDDEClassName = ClassName.getDDEName(className);
        EnhancedClassData enhancedClassData = MotechClassPool.getEnhancedData(realDDEClassName);

        if (enhancedClassData == null) {
            LOG.debug("{} ");
        } else {

        }

        wovenClass.setBytes();
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setMdsJDOEnhancer(MdsJDOEnhancer mdsJDOEnhancer) {
        this.mdsJDOEnhancer = mdsJDOEnhancer;
    }
}
