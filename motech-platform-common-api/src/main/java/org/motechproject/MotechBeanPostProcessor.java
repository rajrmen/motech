package org.motechproject;

public class MotechBeanPostProcessor {

    public MotechBeanPostProcessor() {
        System.out.println("Hello Bean Post Processor. " + this.getClass().getClassLoader());
    }
}









/*
implements BeanPostProcessor {

    private static Logger logger = Logger.getLogger(MotechBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.info("Bean [" + beanName + "] created : " + bean.toString());
        logger.info("Classpath: " + bean.getClass().getClassLoader());
        return bean;
    }
}
*/
