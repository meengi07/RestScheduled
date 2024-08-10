package org.rest.api.restscheduled;

import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RestScheduledBeanProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RestSchedulerManager restSchedulerManager;

    public RestScheduledBeanProcessor(ApplicationContext applicationContext,
        RestSchedulerManager restSchedulerManager) {
        this.applicationContext = applicationContext;
        this.restSchedulerManager = restSchedulerManager;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        for (Method method : targetClass.getDeclaredMethods()) {
            RestScheduled annotation = method.getAnnotation(RestScheduled.class);
            if (annotation != null) {
                String taskName = annotation.name();
                String cronExpression = annotation.cron();
                restSchedulerManager.scheduleTask(taskName, () -> {
                    try {
                        method.setAccessible(true);
                        method.invoke(applicationContext.getBean(targetClass));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, cronExpression);
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @PostConstruct
    public void init() {
        this.restSchedulerManager = applicationContext.getBean(RestSchedulerManager.class);
    }
}
