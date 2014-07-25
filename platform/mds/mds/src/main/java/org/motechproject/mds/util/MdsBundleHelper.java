package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Static utility class that should ease operations on bundles.
 */
public final class MdsBundleHelper {

    /**
     * This method checks whether the bundle is an MDS dependent bundle.
     * It does this by checking the manifest for an org.motechproject.mds import.
     * @return true if bundle imports any mds packages, false otherwise
     */
    public static boolean isMdsDependentBundle(Bundle bundle) {
        BundleHeaders headers = new BundleHeaders(bundle);
        String imports = (String) headers.get(org.osgi.framework.Constants.IMPORT_PACKAGE);
        return StringUtils.contains(imports, "org.motechproject.mds");
    }

    public static Bundle retrieveMdsEntitiesBundle(BundleContext bundleContext) {
        return OsgiBundleUtils.findBundleBySymbolicName(bundleContext, Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
    }

    public static Bundle retrieveMdsBundle(BundleContext bundleContext) {
        return OsgiBundleUtils.findBundleBySymbolicName(bundleContext, Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME);
    }

    public static boolean isMdsEntitiesBundle(Bundle bundle) {
        return nullSafeSymbolicNameMatch(bundle, Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
    }

    public static boolean isMdsBundle(Bundle bundle) {
        return nullSafeSymbolicNameMatch(bundle, Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME);
    }

    private static boolean nullSafeSymbolicNameMatch(Bundle bundle, String symbolicName) {
        return bundle != null && symbolicName.equals(bundle.getSymbolicName());
    }

    private MdsBundleHelper() {
    }
}
