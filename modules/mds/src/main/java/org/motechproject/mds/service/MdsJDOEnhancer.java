package org.motechproject.mds.service;

import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.server.config.SettingsFacade;

public class MdsJDOEnhancer extends JDOEnhancer {
    private static final String DATANUCLEUS_PROPERTIES = "datanucleus.properties";

    public MdsJDOEnhancer(SettingsFacade settingsFacade, ClassLoader classLoader) {
        super(settingsFacade.getProperties(DATANUCLEUS_PROPERTIES));

        setVerbose(true);
        setClassLoader(classLoader);
    }
}
