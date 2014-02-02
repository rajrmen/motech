package org.motechproject.mds.builder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.builder.impl.EntityBuilderImpl;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityBuilderTest {

    private static final String ENTITY_NAME = "BuilderTest";

    private EntityBuilder entityBuilder = new EntityBuilderImpl();

    private MDSClassLoader mdsClassLoader;

    @Mock
    private EntityMapping entity;

    @Before
    public void setUp() {
        mdsClassLoader = new MDSClassLoader(getClass().getClassLoader());
        when(entity.getClassName()).thenReturn(ENTITY_NAME);
    }

    @Test
    public void shouldBuildAnEntityWithFields() throws Exception {
        when(entity.getFields()).thenReturn(asList(field("count", Integer.class),
                field("time", Time.class), field("str", String.class), field("dec", Double.class),
                field("bool", Boolean.class), field("date", Date.class), field("dt", DateTime.class),
                field("list", List.class)));

        Class<?> clazz = buildClass();

        assertNotNull(clazz);
        assertField(clazz, "count", Integer.class);
        assertField(clazz, "time", Time.class);
        assertField(clazz, "str", String.class);
        assertField(clazz, "dec", Double.class);
        assertField(clazz, "bool", Boolean.class);
        assertField(clazz, "date", Date.class);
        assertField(clazz, "dt", DateTime.class);
        assertField(clazz, "list", List.class);
    }

    @Test
    public void shouldBuildAnEntityWithFieldsWithDefaultValues() throws Exception {
        final Date date = new Date();
        final DateTime dateTime = DateUtil.now();

        when(entity.getFields()).thenReturn(asList(field("count", Integer.class, 1),
                field("time", Time.class, new Time(10, 10)), field("str", String.class, "defStr"),
                field("dec", Double.class, 3.1), field("bool", Boolean.class, true),
                field("date", Date.class, date), field("dt", DateTime.class, dateTime),
                field("list", List.class, asList("1", "2", "3"))));

        Class<?> clazz = buildClass();

        assertNotNull(clazz);
        assertField(clazz, "count", Integer.class, 1);
        assertField(clazz, "time", Time.class, new Time(10, 10));
        assertField(clazz, "str", String.class, "defStr");
        assertField(clazz, "dec", Double.class, 3.1);
        assertField(clazz, "bool", Boolean.class, true);
        assertField(clazz, "date", Date.class, date);
        assertField(clazz, "dt", DateTime.class, dateTime);
        assertField(clazz, "list", List.class, asList("1", "2", "3"));
    }

    @Test
    public void shouldEditClasses() throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        when(entity.getFields()).thenReturn(asList(field("name", Integer.class)));

        Class<?> clazz = buildClass();
        assertField(clazz, "name", Integer.class);

        when(entity.getFields()).thenReturn(asList(field("name2", String.class)));

        // reload the classloader for class edit
        mdsClassLoader = new MDSClassLoader(getClass().getClassLoader());

        clazz = buildClass();
        assertField(clazz, "name2", String.class);

        // assert that the first field no longer exists
        try {
            clazz.getDeclaredField("name");
            fail("Field 'name' was preserved in the class, although it was removed from the entity");
        } catch (NoSuchFieldException e) {
            // expected
        }
    }

    private Class<?> buildClass() {
        ClassData classData = entityBuilder.build(entity);

        assertEquals(ENTITY_NAME, classData.getClassName());

        return mdsClassLoader.defineClass(classData.getClassName(), classData.getBytecode());
    }

    private void assertField(Class<?> clazz, String name, Class<?> fieldType)
            throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        assertField(clazz, name, fieldType, null);
    }

    private void assertField(Class<?> clazz, String name, Class<?> fieldType, Object expectedDefaultVal)
            throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = clazz.getDeclaredField(name);

        assertNotNull(field);
        assertEquals(Modifier.PRIVATE, field.getModifiers());
        assertEquals(fieldType, field.getType());

        Object instance = clazz.newInstance();
        Object val = ReflectionTestUtils.getField(instance, name);
        assertEquals(expectedDefaultVal, val);
    }

    private FieldMapping field(String name, Class<?> typeClass) {
        return field(name, typeClass, null);
    }

    private FieldMapping field(String name, Class<?> typeClass, Object defaultVal) {
        AvailableFieldTypeMapping type = new AvailableFieldTypeMapping();
        // we only need the type
        type.setTypeClass(typeClass.getName());

        FieldMapping field = new FieldMapping();
        // we only need the name, type and default value
        field.setName(name);
        field.setType(type);
        field.setDefaultValue(type.format(defaultVal));

        return field;
    }
}
