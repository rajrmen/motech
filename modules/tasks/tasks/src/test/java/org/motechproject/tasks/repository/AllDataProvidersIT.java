package org.motechproject.tasks.repository;

import com.google.gson.reflect.TypeToken;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.DataProvider;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllDataProvidersIT extends SpringIntegrationTest {

    @Autowired
    private AllDataProviders allDataProviders;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Test
    public void shouldAddDataProvider() {
        DataProvider expected = loadDataProvider();

        allDataProviders.addOrUpdate(expected);

        DataProvider actual = allDataProviders.byName("MRS");

        assertEquals(expected, actual);

        markForDeletion(actual);
    }

    private DataProvider loadDataProvider() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream mrsDataProviderStream = classLoader.getResourceAsStream("mrs-test-data-provider.json");
        Type type = new TypeToken<DataProvider>() { }.getType();

        return (DataProvider) motechJsonReader.readFromStream(mrsDataProviderStream, type);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
