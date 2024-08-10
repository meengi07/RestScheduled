# RestScheduled

## 프로젝트 설명

**RestScheduled**는 Spring 기반 애플리케이션에서 커스텀 스케줄링 기능을 제공하는 라이브러리입니다. 이 라이브러리는 `@RestScheduled` 어노테이션을 사용하여 작업을 주기적으로 실행하고, `SchedulerManager`를 통해 작업을 동적으로 관리할 수 있는 기능을 제공합니다.

### 주요 기능:
- `@RestScheduled` 어노테이션을 사용하여 메서드를 간단히 스케줄링
- `SchedulerManager`를 통해 작업을 시작, 중지, 재시작 등의 관리 가능
- 커스텀 스케줄러 설정 가능

## 설치 방법

Maven 또는 Gradle을 사용하여 라이브러리를 프로젝트에 추가할 수 있습니다.

### Maven

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>rest-scheduled</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.example:rest-scheduled:1.0.0'
```


## 실행 방법
1. 라이브러리 의존성 추가: 위의 설치 방법에 따라 라이브러리를 프로젝트에 추가합니다.

2. 스케줄링 활성화: 애플리케이션에서 @EnableScheduling 어노테이션을 추가하여 스케줄링을 활성화합니다.
    ```java
    import org.springframework.context.annotation.Configuration;
    import org.springframework.scheduling.annotation.EnableScheduling;
    
    @Configuration
    @EnableScheduling
    public class AppConfig {
    } 
    ```
3. 스케줄링할 메서드 정의: @RestScheduled 어노테이션을 사용하여 스케줄링할 메서드를 정의합니다.

    ```java
    import com.example.restscheduled.RestScheduled;
    import org.springframework.stereotype.Component;
    
    @Component
    public class MyScheduledTasks {
    
        @RestScheduled(name = "myTask", cron = "0/5 * * * * *")
        public void executeTask() {
            System.out.println("This task runs every 5 seconds");
        }
    }
    ```
4. 애플리케이션 실행: 애플리케이션을 실행하면 executeTask 메서드가 5초마다 실행됩니다.

## 설정 방법
1. TaskScheduler 설정 <br/> 
   사용자가 직접 TaskScheduler를 설정하여 스레드 풀 크기 등을 조정할 수 있습니다.
    ```java
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
    
    @Configuration
    public class CustomSchedulerConfig {
    
        @Bean
        public ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(10);  // 스레드 풀 크기 설정
            taskScheduler.setThreadNamePrefix("custom-scheduled-task-");
            return taskScheduler;
        }
    }
    
    ```
    <br/> 
2. SchedulerManager 사용 <br/> 
SchedulerManager를 사용하여 스케줄링된 작업을 제어할 수 있습니다.

    - 작업 중지 
    ```java
    @Autowired
    private SchedulerManager schedulerManager;
    
    public void stopTask(String taskName) {
        schedulerManager.stopTask(taskName);
    }
    ```
    - 작업 재시작
    ```java
    public void startTask(String taskName) {
        schedulerManager.startTask(taskName);
    }
    ```

    - 작업 실행 여부 확인
    
    ```java
    public boolean isTaskRunning(String taskName) {
        return schedulerManager.isTaskRunning(taskName);
    }
    ```


