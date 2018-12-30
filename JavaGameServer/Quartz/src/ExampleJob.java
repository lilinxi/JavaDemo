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

