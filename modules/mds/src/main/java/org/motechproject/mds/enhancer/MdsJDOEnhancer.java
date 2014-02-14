package org.motechproject.mds.enhancer;

import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.server.config.SettingsFacade;

import static org.motechproject.mds.util.Constants.Config;

/**
 * The <code>MdsJDOEnhancer</code> class is a wrapper for
 * {@link org.datanucleus.api.jdo.JDOEnhancer} class. Its task is to add the missing information
 * into created entity class.
 */
public class MdsJDOEnhancer extends JDOEnhancer {

    public MdsJDOEnhancer(SettingsFacade settingsFacade, ClassLoader classLoader) {
        super(settingsFacade.getProperties(Config.DATANUCLEUS_FILE));
        setClassLoader(classLoader);
        setVerbose(true);
    }
}
