package com.smalleyes.aws.util;


import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文，用于SpringBean实例获取
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    private static void checkIsInject() {
        if (null == applicationContext) {
            throw new NullPointerException("applicationContext is not init, please check!");
        }
    }

    /**
     * 获取Spring托管的实例
     *
     * @param clazz Class对象
     * @return Spring托管的实例
     */
    public static <T> T getBean(Class<T> clazz) {

        checkIsInject();

        return applicationContext.getBean(clazz);
    }

    public static String getMessage(String code, Object[] args, Locale locale) {

        checkIsInject();

        return applicationContext.getMessage(code, args, locale);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {

        checkIsInject();

        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {

        checkIsInject();

        return applicationContext.getBean(beanName, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        checkIsInject();
        return applicationContext.getBeansOfType(clazz);
    }

}