package org.motechproject.commons.couchdb.osgi;

import org.ektorp.support.GenerateView;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.dao.BusinessIdNotUniqueException;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.util.List;

public class CommonsCouchDBBundleIT extends BaseOsgiIT {

    public void testCommonsCouchDB() throws Exception {
        TestRepository repository = new TestRepository();
        repository.addOrReplace(new TestRecord("test"));
        repository.add(new TestRecord("test"));
        try {
            repository.addOrReplace(new TestRecord("test"));
            fail("Expected BusinessIdNotUniqueException");
        } catch (BusinessIdNotUniqueException e) {
        } finally {
            repository.removeAll();
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/testApplicationCommonsCouchdbBundleContext.xml"};
    }

    class TestRepository extends MotechBaseRepository<TestRecord> {
        protected TestRepository() {
            super("testDb", TestRecord.class);
        }

        protected void addOrReplace(TestRecord entity) {
            super.addOrReplace(entity, "name", entity.getName());
        }

        @GenerateView
        List<TestRecord> findByName(String name) {
            return queryView("by_name", name);
        }
    }

    @TypeDiscriminator("doc.type === 'TestRecord'")
    public static class TestRecord extends MotechBaseDataObject {
        private String name;

        public TestRecord() {
        }

        public TestRecord(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
