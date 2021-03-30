package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import tech.turl.community.service.AlphaService;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTests.class);
    // 1.JDK普通线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // 2.JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // 3.Spring普通线程池
    @Autowired private ThreadPoolTaskExecutor taskExecutor;

    // 4.Spring可执行定时任务的线程池
    @Autowired private ThreadPoolTaskScheduler taskScheduler;

    @Autowired private AlphaService alphaService;

    /**
     * 封装的休眠函数
     *
     * @param m
     */
    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** 1.JDK普通线程池执行任务 */
    @Test
    public void testExecutorService() {
        Worker worker = new Worker("ExecutorService");
        for (int i = 0; i < 10; ++i) {
            executorService.execute(worker);
        }
        sleep(10000);
    }

    /** 2.JDK定时任务线程池 */
    @Test
    public void testScheduledExecutorService() {
        Worker worker = new Worker("ScheduledExecutorService");
        scheduledExecutorService.scheduleAtFixedRate(worker, 10000, 1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    /** 3.Spring普通线程池 */
    @Test
    public void testThreadPoolTaskExecutor() {
        Worker worker = new Worker("ThreadPoolTaskExecutor");
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(worker);
        }
        sleep(10000);
    }

    /** 4.Spring定时线程池 */
    @Test
    public void testThreadPoolTaskScheduler() {
        Worker worker = new Worker("ThreadPoolTaskScheduler");
        Date startTime = new Date(System.currentTimeMillis() + 10_000);
        taskScheduler.scheduleAtFixedRate(worker, startTime, 1_000);
        sleep(30_000);
    }

    /** 5.Spring普通线程池，简单版本 */
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }
        sleep(10_000);
    }

    /** 6.Spring定时任务线程池(简化) */
    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        sleep(30_000);
    }

    class Worker implements Runnable {
        private String log;

        public Worker(String log) {
            this.log = log;
        }

        @Override
        public void run() {
            LOGGER.debug("hello " + log);
        }
    }
}
