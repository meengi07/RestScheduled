package org.rest.scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
public class RestSchedulerManager {

    private final TaskScheduler taskScheduler;
    private final Map<String, RestScheduledTask> scheduledTasks = new ConcurrentHashMap<>();

    public RestSchedulerManager(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleTask(String name, Runnable task, String cronExpression) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(name, new RestScheduledTask(task, cronExpression, scheduledFuture));
    }

    public void stopTask(String name) {
        RestScheduledTask scheduledTask = scheduledTasks.get(name);
        if (scheduledTask != null && scheduledTask.getScheduledFuture() != null) {
            boolean cancel = scheduledTask.getScheduledFuture().cancel(false);
            if(cancel) {
                scheduledTask.setScheduledFuture(null);
            }
        }
    }

    public void startTask(String name) {
        RestScheduledTask scheduledTask = scheduledTasks.get(name);
        if (scheduledTask != null && scheduledTask.getScheduledFuture() == null) {
            ScheduledFuture<?> newScheduledFuture = taskScheduler.schedule(
                scheduledTask.getTask(),
                new CronTrigger(scheduledTask.getCronExpression())
            );
            scheduledTask.setScheduledFuture(newScheduledFuture);
        }
    }

    public boolean isTaskRunning(String name) {
        RestScheduledTask scheduledTask = scheduledTasks.get(name);
        return scheduledTask != null && scheduledTask.getScheduledFuture() != null && !scheduledTask.getScheduledFuture().isCancelled();
    }

    public List<String> getTaskNames() {
        return new ArrayList<>(scheduledTasks.keySet());
    }
}
