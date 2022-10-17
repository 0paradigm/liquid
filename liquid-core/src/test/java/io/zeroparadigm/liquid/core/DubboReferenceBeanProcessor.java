package io.zeroparadigm.liquid.core;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.Reference;
import org.springframework.stereotype.Component;

public class DubboReferenceBeanProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        List<Field> annotatedFields = FieldUtils.getFieldsListWithAnnotation(bean.getClass(), Reference.class);
        annotatedFields.addAll(FieldUtils.getFieldsListWithAnnotation(bean.getClass(), com.alibaba.dubbo.config.annotation.Reference.class));
        annotatedFields.addAll(FieldUtils.getFieldsListWithAnnotation(bean.getClass(), DubboReference.class));
        for (Field field : annotatedFields) {
            try {
                Class<?> type = field.getType();
                String mockBeanName = BeanFactoryUtils.transformedBeanName(type.getSimpleName());
                if (!beanFactory.containsBean(mockBeanName)) {
                    beanFactory.registerSingleton(mockBeanName, Mockito.mock(type));
                }

                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(bean, beanFactory.getBean(type));
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
            }
        }
        return bean;
    }
}