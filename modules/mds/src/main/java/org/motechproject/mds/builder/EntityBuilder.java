package org.motechproject.mds.builder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.motechproject.mds.ex.EntityBuilderException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The <code>EntityBuilder</code> is use to create a new empty class in a given class loader.
 */
@Component
public class EntityBuilder {
    private final MDSClassLoader classLoader = MDSClassLoader.PERSISTENCE;

    private final ClassPool classPool = MotechClassPool.getDefault();

    public byte[] build(EntityMapping entityMapping) {
        try {
            String className = entityMapping.getClassName();

            CtClass ctClass = classPool.makeClass(entityMapping.getClassName());

            for (FieldMapping field : entityMapping.getFields()) {
                AvailableFieldTypeMapping fieldType = field.getType();

                CtClass type = MotechClassPool.getDefault().get(fieldType.getTypeClass());

                CtField ctField = new CtField(type, field.getName(), ctClass);
                ctField.setModifiers(Modifier.PRIVATE);

                if (StringUtils.isBlank(field.getDefaultValue())) {
                    ctClass.addField(ctField);
                } else {
                    ctClass.addField(ctField, initializerForField(field));
                }
            }

            byte[] classBytes = ctClass.toBytecode();
            classLoader.defineClass(className, classBytes);

            return classBytes;
        } catch (Exception e) {
            throw new EntityBuilderException(e);
        }
    }

    private CtField.Initializer initializerForField(FieldMapping field) throws NotFoundException {
        AvailableFieldTypeMapping fieldType = field.getType();
        String typeClass = fieldType.getTypeClass();

        Object defaultValue = fieldType.parse(field.getDefaultValue());

        switch (typeClass) {
            case "java.util.List":
                return listInitializer(defaultValue);
            case "java.lang.Integer":
            case "java.lang.Double":
            case "java.lang.Boolean":
                 return newInitializer(typeClass, defaultValue);
            case "java.lang.String":
                return CtField.Initializer.constant((String) defaultValue);
            case "org.motechproject.commons.date.model.Time":
                Time time  = (Time) defaultValue;
                return newInitializer(typeClass, '"' + time.timeStr() + '"');
     /*       case "org.joda.time.DateTime":
                DateTime dateTime = (DateTime) defaultValue; new DateTime()
                return CtField.Initializer.byNewWithParams(classPool.get(DateTime.class.getName()), )*/
            default:
                return null;
        }
    }

    private CtField.Initializer listInitializer(Object defaultValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("new ").append(ArrayList.class.getName()).append('(');
        sb.append(Arrays.class.getName()).append(".asList(");

        List defValList = (List) defaultValue;
        for (Object obj : defValList) {
            sb.append(obj).append(", ");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("))");

        return CtField.Initializer.byExpr(sb.toString());
    }

    private CtField.Initializer newInitializer(String type, Object defaultValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("new ").append(type).append('(')
          .append(defaultValue.toString()).append(')');

        return CtField.Initializer.byExpr(sb.toString());
    }
}
