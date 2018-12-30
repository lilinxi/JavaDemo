Quartz 是一个功能强大的作业调度工具，相当于数据库中的 Job、Windows 的计划任务、Unix/Linux 下的 Cron，但 Quartz 可以把流程控制的更精细。而且 Quartz1.✘ 版本可以和 Spring 进行结合。

本文使用的 Quartz：

```
 <groupId>org.quartz-scheduler</groupId>
 <artifactId>quartz</artifactId>
 <version>2.3.0</version>
```

参考教程：https://blog.csdn.net/yangshangwei/column/info/17954

Quartz 中的 Log 功能依赖于 slf4j：

- org.slf4j:slf4j-api
- org.slf4j:slf4f-simple

- 定义一个简单的 Job 类：

```
import org.quartz.*;

import java.util.Date;

public class ExampleJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobKey key = jobExecutionContext.getJobDetail().getKey();
        System.out.println("Instance: "+key+"------------------");
        System.out.println("Date: " + new Date());
        JobDataMap properties = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println("float: " + properties.getFloat("float"));
        System.out.println("Previous Fire Time: " + jobExecutionContext.getPreviousFireTime());// 上次执行时间
        System.out.println("Current Fire Time: " + jobExecutionContext.getFireTime());// 本次执行时间
        System.out.println("Next Fire Time: " + jobExecutionContext.getNextFireTime());// 下一次执行时间
    }
}


```

- 定义了属性和 Get Set 函数的 Job 类：

```
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * 具体执行的任务
 */
public class ClassJob implements Job {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey key = context.getJobDetail().getKey();
        System.out.println("Instance " + key + ",姓名：" + name + ",年龄：" + age);
    }
}
```

- 使用示例：

```
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 依赖于：
 * org.slf4j:slf4j-api
 * org.slf4j:slf4f-simple
 *
 * 教程：
 * https://blog.csdn.net/yangshangwei/column/info/17954
 */

public class Console {
    public static void main(String[] args) {
        try {
            testJob();
            testClassJob();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 测试使用quartz实现的调度任务
    public static void testJob() throws SchedulerException, InterruptedException {
        // 创建调度者工厂
        SchedulerFactory sfc = new StdSchedulerFactory();
        // 通过工厂创建一个调度者
        Scheduler scheduler = sfc.getScheduler();
        //----------Quartz 2.3.0的写法---------------//
        JobDetail myJob = newJob(ExampleJob.class)
                .withIdentity("myJob", "job-group")
                .usingJobData("float", 3.14f)
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("mytrigger", "trigger-group")
                .startAt(new Date())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(3)
                        .repeatForever())
                .build();// 调度任务，每隔一定时间执行
        Date startDate = scheduler.scheduleJob(myJob, trigger);
        System.out.println(myJob.getKey() + " will start at:" + startDate);
        // 开始运行调度程序
        scheduler.start();
        Thread.sleep(10000);// 等待一定时间
        scheduler.shutdown();// 关闭调度程序
        SchedulerMetaData metaData = scheduler.getMetaData();
        System.out.println("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
        System.out.println("Test end------>");
    }

    public static void testClassJob() throws SchedulerException, InterruptedException {
        SchedulerFactory sfc = new StdSchedulerFactory();
        Scheduler scheduler = sfc.getScheduler();
        JobDetail myJob = newJob(ClassJob.class)
                .withIdentity("myJob", "job-group")
                .usingJobData("name", "User")
                .usingJobData("age",12)
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("mytrigger", "trigger-group")
                .startAt(new Date())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(3)
                        .repeatForever())
                .build();
        Date startDate = scheduler.scheduleJob(myJob, trigger);
        System.out.println(myJob.getKey() + " will start at:" + startDate);
        scheduler.start();
        Thread.sleep(10000);// 等待一定时间
        scheduler.shutdown();// 关闭调度程序
        SchedulerMetaData metaData = scheduler.getMetaData();
        System.out.println("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
        System.out.println("Test end------>");
    }
}

```