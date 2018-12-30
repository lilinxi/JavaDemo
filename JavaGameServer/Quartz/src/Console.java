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
