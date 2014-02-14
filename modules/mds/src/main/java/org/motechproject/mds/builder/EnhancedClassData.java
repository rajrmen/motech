package org.motechproject.mds.builder;

import javax.jdo.metadata.ClassMetadata;

/**
 * Apart from fields inherited from {@link org.motechproject.mds.builder.ClassData}, this class
 * also holds information about the JDO metadata for a class.
 */
public class EnhancedClassData extends ClassData {

    private final ClassMetadata classMetadata;

    public EnhancedClassData(String className, byte[] bytecode, ClassMetadata classMetadata) {
        super(className, bytecode);
        this.classMetadata = classMetadata;
    }

    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }
}
