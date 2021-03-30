package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTests {
    @Autowired private Scheduler scheduler;

    @Test
    public void testDeleteJob() throws SchedulerException {
        boolean result = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
        System.out.println(result);
    }
}
