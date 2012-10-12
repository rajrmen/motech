package org.motechproject.testing.utils.osgi;

import org.apache.log4j.Logger;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.ektorp.DbInfo;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.osgi.framework.Bundle;

public class EktorpOsgiIT extends BaseOsgiIT {

    private Logger logger = Logger.getLogger(EktorpOsgiIT.class);

    public void testOsgiPlatformStarts() {
        final StdCouchDbConnector test = new StdCouchDbConnector("test", new StdCouchDbInstance( new StdHttpClient.Builder().caching(false).build()));
        test.createDatabaseIfNotExists();
        final DbInfo dbInfo = test.getDbInfo();
        System.out.print(dbInfo);
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            System.out.print(OsgiStringUtils.nullSafeSymbolicName(bundles[i]));
            System.out.print("\n ");
        }
        System.out.print("\n");
    }
}
