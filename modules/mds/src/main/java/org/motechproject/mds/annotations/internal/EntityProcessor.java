package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.ClassName;
import org.motechproject.osgi.web.BundleHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.motechproject.mds.util.Constants.AnnotationFields.MODULE;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAMESPACE;

/**
 * The <code>EntityProcessor</code> provides a mechanism to adding public classes from other
 * modules as entities in the MDS module. When the entity is successfully added into MDS database,
 * related processors are starting to process the class definitions in order to add other
 * information into MDS database.
 *
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class EntityProcessor extends AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityProcessor.class);

    private EntityService entityService;
    private FieldProcessor fieldProcessor;

    private BundleHeaders bundleHeaders;

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Entity.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return AnnotationsUtil.getClasses(getAnnotation(), getBundle());
    }

    @Override
    protected void process(AnnotatedElement element) {
        if (null == bundleHeaders) {
            bundleHeaders = new BundleHeaders(getBundle());
        }

        Class clazz = (Class) element;
        Entity annotation = AnnotationUtils.findAnnotation(clazz, Entity.class);

        if (null != annotation) {
            String className = clazz.getName();

            String name = AnnotationsUtil.getAnnotationValue(
                    annotation, NAME, ClassName.getSimpleName(className)
            );
            String module = AnnotationsUtil.getAnnotationValue(
                    annotation, MODULE, bundleHeaders.getName(), bundleHeaders.getSymbolicName()
            );
            String namespace = AnnotationsUtil.getAnnotationValue(annotation, NAMESPACE);

            try {
                EntityDto fromDb = entityService.getEntityByClassName(className);

                if (fromDb == null) {
                    LOGGER.debug("Creating DDE for {}", className);

                    EntityDto entity = new EntityDto(className, name, module, namespace);
                    fromDb = entityService.createEntity(entity);
                } else {
                    LOGGER.debug("DDE for {} already exists, updating if necessary", className);
                }

                findFields(clazz, fromDb);
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create an entity for class {} from bundle {}",
                        clazz.getName(), getBundle().getSymbolicName()
                );
                LOGGER.error("because of: ", e);
            }
        } else {
            LOGGER.debug("Did not find Entity annotation in class: {}", clazz.getName());
        }
    }

    private void findFields(Class clazz, EntityDto entity) {
        fieldProcessor.setClazz(clazz);
        fieldProcessor.setEntity(entity);
        fieldProcessor.execute();

        entityService.addFields(entity, fieldProcessor.getFields());
    }

    private String getName(Entity annotation, Class clazz) {
        return defaultIfBlank(annotation.name(), ClassName.getSimpleName(clazz.getName()));
    }

    private String getModule(Entity annotation) {
        BundleHeaders headers = new BundleHeaders(getBundle());

        String module = defaultIfBlank(annotation.module(), headers.getName());
        module = defaultIfBlank(module, headers.getSymbolicName());
        module = defaultIfBlank(module, null);

        return module;
    }

    private String getNamespace(Entity annotation) {
        return defaultIfBlank(annotation.namespace(), null);
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setFieldProcessor(FieldProcessor fieldProcessor) {
        this.fieldProcessor = fieldProcessor;
    }
}
