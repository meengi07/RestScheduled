package org.rest.scheduled;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestSchedulerManagerTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private ScheduledFuture<Void> scheduledFuture;

    private RestSchedulerManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new RestSchedulerManager(taskScheduler);
    }

    @Test
    void 스케줄링_동작_테스트() {
        String name = "testTask";
        Runnable task = () -> {};
        String cronExpression = "0 0 * * * ?";

        ScheduledFuture<Void> schedule = (ScheduledFuture<Void>) taskScheduler.schedule(task, new CronTrigger(cronExpression));
        when(schedule).thenReturn(scheduledFuture);

        manager.scheduleTask(name, task, cronExpression);

        verify(taskScheduler).schedule(eq(task), any(CronTrigger.class));
        boolean result = manager.isTaskRunning(name);
        Assertions.assertTrue(result);
    }

    @Test
    void 스케줄러_정지_테스트() {
        String name = "nonExistentTask";

        manager.stopTask(name);

        assertFalse(manager.isTaskRunning(name));
    }

    @Test
    void 스케줄러_재시작_테스트() {
        String name = "nonExistentTask";

        manager.startTask(name);

        assertFalse(manager.isTaskRunning(name));
    }

    @Test
    void 스케줄러_중복등록_테스트() {
        String name = "duplicateTask";
        Runnable task = () -> {};
        String cronExpression = "0 0 * * * ?";
        ScheduledFuture<Void> schedule = (ScheduledFuture<Void>) taskScheduler.schedule(any(Runnable.class),
            any(CronTrigger.class));

        when(schedule).thenReturn(scheduledFuture);

        manager.scheduleTask(name, task, cronExpression);
        manager.scheduleTask(name, task, cronExpression);

        List<String> taskNames = manager.getTaskNames();
        assertEquals(1, taskNames.size());
        assertTrue(taskNames.contains(name));
    }

}