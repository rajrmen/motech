/*
package org.motechproject.mds.builder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.PackageMetadata;
import java.util.Arrays;
import java.util.List;

import static javax.jdo.annotations.IdentityType.DATASTORE;
import static javax.jdo.metadata.ClassPersistenceModifier.PERSISTENCE_CAPABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EntityMetadataBuilderIT extends BaseIT {
    private static final String PACKAGE = "org.motechproject.mdsgenerated.entity";
    private static final String ENTITY_NAME = "Sample";
    private static final String MODULE = "MrS";
    private static final String NAMESPACE = "arrio";

    private static final String CLASS_NAME = String.format("%s.%s", PACKAGE, ENTITY_NAME);
    private static final String TABLE_NAME_1 = ENTITY_NAME.toUpperCase();
    private static final String TABLE_NAME_2 = String.format("%s_%s", MODULE, ENTITY_NAME).toUpperCase();
    private static final String TABLE_NAME_3 = String.format("%s_%s_%s", MODULE, NAMESPACE, ENTITY_NAME).toUpperCase();

    @Autowired
    private EntityMetadataBuilder entityMetadataBuilder;

    @Before
    public void setUp() {
        clearDB();
    }

    @After
    public void tearDown() {
        clearDB();
    }

*/
/*    @Test
    public void shouldCreateBaseEntity() throws Exception {
        Entity entity = new Entity();
        entity.setClassName(CLASS_NAME);

        //entityMetadataBuilder.addEntityMetadata(mainMetadata, entity);


        //assertEquals(1, entityMetadataBuilder.getJdoMetadata().getNumberOfPackages());

        PackageMetadata packageMetadata = entityMetadataBuilder.getJdoMetadata().getPackages()[0];

        assertEquals(PACKAGE, packageMetadata.getName());

        List<ClassMetadata> classMetadata = Arrays.asList(packageMetadata.getClasses());
        ClassMetadata metadata = null;
        for (ClassMetadata cm : classMetadata) {
            if (ENTITY_NAME.equals(cm.getName())) {
                metadata = cm;
                break;
            }
        }

        assertNotNull(metadata);
        assertEquals(TABLE_NAME_1, metadata.getTable());
        assertTrue(metadata.getDetachable());
        assertEquals(DATASTORE, metadata.getIdentityType());
        assertEquals(PERSISTENCE_CAPABLE, metadata.getPersistenceModifier());*//*

    }

    @Test
    public void shouldSetAppropriateTableName() throws Exception {
        assertEquals(TABLE_NAME_1, getClassMetadata(null, null).getTable());
        assertEquals(TABLE_NAME_2, getClassMetadata(MODULE, null).getTable());
        assertEquals(TABLE_NAME_3, getClassMetadata(MODULE, NAMESPACE).getTable());
    }

    private ClassMetadata getClassMetadata(String module, String namespace) {
        Entity entity = new Entity();
        entity.setClassName(CLASS_NAME);
        entity.setModule(module);
        entity.setNamespace(namespace);

        entityMetadataBuilder.addEntityMetadata(null, entity);
        return mainMetadata.getPackages()[0].getClasses()[0];*//*


    }
}
*/
