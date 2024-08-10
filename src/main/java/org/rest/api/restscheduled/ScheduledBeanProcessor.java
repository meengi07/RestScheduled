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
public class ScheduledBeanProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private SchedulerManager schedulerManager;

    public ScheduledBeanProcessor(ApplicationContext applicationContext,
        SchedulerManager schedulerManager) {
        this.applicationContext = applicationContext;
        this.schedulerManager = schedulerManager;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        for (Method method : targetClass.getDeclaredMethods()) {
            RestScheduled annotation = method.getAnnotation(RestScheduled.class);
            if (annotation != null) {
                String taskName = annotation.name();
                String cronExpression = annotation.cron();
                schedulerManager.scheduleTask(taskName, () -> {
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
        this.schedulerManager = applicationContext.getBean(SchedulerManager.class);
    }
}
