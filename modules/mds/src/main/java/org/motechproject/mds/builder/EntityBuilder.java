package org.motechproject.mds.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.motechproject.mds.ex.EntityBuilderException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

            addFields(ctClass, entityMapping.getFields());

            byte[] classBytes = ctClass.toBytecode();
            classLoader.defineClass(className, classBytes);

            return classBytes;
        } catch (Exception e) {
            throw new EntityBuilderException(e);
        }
    }

    private void addFields(CtClass ctClass, List<FieldMapping> fields) throws NotFoundException, CannotCompileException {
        for (FieldMapping field : fields) {
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
            case "org.joda.time.DateTime":
                DateTime dateTime = (DateTime) defaultValue;
                return newInitializer(typeClass, dateTime.getMillis()  + "l"); // explicit long
            case "java.util.Date":
                Date date = (Date) defaultValue;
                return newInitializer(typeClass, date.getTime() + "l"); // explicit long
            default:
                return null;
        }
    }

    private CtField.Initializer listInitializer(Object defaultValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("new ").append(ArrayList.class.getName()).append('(');
        sb.append(Arrays.class.getName()).append(".asList(new Object[]{");

        List defValList = (List) defaultValue;

        for (int i = 0; i < defValList.size(); i++) {
            Object obj = defValList.get(i);
            // list of strings
            sb.append('\"').append(obj).append('\"');

            if (i < defValList.size() - 1) {
                sb.append(',');
            }
        }

        sb.append("}))");

        return CtField.Initializer.byExpr(sb.toString());
    }

    private CtField.Initializer newInitializer(String type, Object defaultValue) {
        return CtField.Initializer.byExpr("new " + type + '(' + defaultValue.toString() + ')');
    }
}
