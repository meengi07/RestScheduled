package org.rest.scheduled;

import java.util.concurrent.ScheduledFuture;

public class RestScheduledTask {
    private final Runnable task;
    private final String cronExpression;
    private ScheduledFuture<?> scheduledFuture;

    public RestScheduledTask(Runnable task, String cronExpression, ScheduledFuture<?> scheduledFuture) {
        this.task = task;
        this.cronExpression = cronExpression;
        this.scheduledFuture = scheduledFuture;
    }

    public Runnable getTask() { return task; }
    public String getCronExpression() { return cronExpression; }
    public ScheduledFuture<?> getScheduledFuture() { return scheduledFuture; }
    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

}
