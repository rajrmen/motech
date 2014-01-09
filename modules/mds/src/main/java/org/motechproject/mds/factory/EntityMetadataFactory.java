package org.motechproject.mds.factory;

import org.motechproject.mds.builder.ClassName;

import javax.jdo.annotations.IdentityType;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.PackageMetadata;

public final class EntityMetadataFactory {

    private EntityMetadataFactory() {
    }

    public static JDOMetadata createBaseEntity(JDOMetadata md, String className) {
        ClassName clazz = new ClassName(className);

        PackageMetadata pmd = md.newPackageMetadata(clazz.getPackage());
        ClassMetadata cmd = pmd.newClassMetadata(clazz.getSimpleName());

        cmd.setTable(clazz.getSimpleName());
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.DATASTORE);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        return md;
    }

}
