package org.motechproject.tasks.domain;

import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.collections.CollectionUtils.find;

@TypeDiscriminator("doc.type == 'DataProvider'")
public class DataProvider extends MotechBaseDataObject {
    private static final long serialVersionUID = -5427486904165895928L;
    private String name;
    private List<DataProviderObject> objects;

    public DataProvider() {
        this(null, new ArrayList<DataProviderObject>());
    }

    public DataProvider(String name, List<DataProviderObject> objects) {
        this.name = name;
        this.objects = objects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public DataProviderObject getObject(final String name) {
        return (DataProviderObject) find(objects, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((DataProviderObject) object).getDisplayName().equalsIgnoreCase(name);
            }
        });
    }

    public List<DataProviderObject> getObjects() {
        return objects;
    }

    public void setObjects(List<DataProviderObject> objects) {
        this.objects = objects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataProvider that = (DataProvider) o;

        return Objects.equals(name, that.name) && Objects.equals(objects, that.objects);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (objects != null ? objects.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("DataProvider{name='%s', objects=%s}", name, objects);
    }
}
