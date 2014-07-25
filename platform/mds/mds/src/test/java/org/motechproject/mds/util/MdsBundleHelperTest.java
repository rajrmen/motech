package org.motechproject.mds.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsBundleHelperTest {

    @Mock
    private Bundle bundle;

    @Mock
    private BundleContext bundleContext;

    @Test
    public void shouldRecognizeMdsBundles() {
        when(bundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        assertTrue(MdsBundleHelper.isMdsEntitiesBundle(bundle));
        assertFalse(MdsBundleHelper.isMdsBundle(bundle));

        when(bundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME);
        assertTrue(MdsBundleHelper.isMdsBundle(bundle));
        assertFalse(MdsBundleHelper.isMdsEntitiesBundle(bundle));
    }

    @Test
    public void shouldRetrieveMdsBundles() {
        when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});

        when(bundle.getSymbolicName()).thenReturn("somethingElse");
        assertNull(MdsBundleHelper.retrieveMdsBundle(bundleContext));
        assertNull(MdsBundleHelper.retrieveMdsEntitiesBundle(bundleContext));

        when(bundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME);
        assertEquals(bundle, MdsBundleHelper.retrieveMdsBundle(bundleContext));
        assertNull(MdsBundleHelper.retrieveMdsEntitiesBundle(bundleContext));

        when(bundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        assertEquals(bundle, MdsBundleHelper.retrieveMdsEntitiesBundle(bundleContext));
        assertNull(MdsBundleHelper.retrieveMdsBundle(bundleContext));
    }

    @Test
    public void shouldRecognizeMdsDependentBundles() {
        Dictionary<String, String> headers = new Hashtable<>();
        headers.put(org.osgi.framework.Constants.IMPORT_PACKAGE, "org.motechproject.security.service;vers" +
                "ion=\"[0.25,1)\",org.motechproject.server.api;version=\"[0.25,1)\", " +
                "org.motechproject.mds.annotations;version=\"[0.25,1)\\");
        when(bundle.getHeaders()).thenReturn(headers);

        assertTrue(MdsBundleHelper.isMdsDependentBundle(bundle));

        headers.put(org.osgi.framework.Constants.IMPORT_PACKAGE, "org.motechproject.security.service;version=\"[0.25,1)\"");
        assertFalse(MdsBundleHelper.isMdsDependentBundle(bundle));

        headers.put(org.osgi.framework.Constants.IMPORT_PACKAGE, "");
        assertFalse(MdsBundleHelper.isMdsDependentBundle(bundle));

        headers.remove(org.osgi.framework.Constants.IMPORT_PACKAGE);
        assertFalse(MdsBundleHelper.isMdsDependentBundle(bundle));

    }
}
