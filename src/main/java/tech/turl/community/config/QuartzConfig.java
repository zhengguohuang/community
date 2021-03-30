package tech.turl.community.config;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import tech.turl.community.quartz.AlphaJob;
import tech.turl.community.quartz.PostScoreRefreshJob;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
@Configuration
public class QuartzConfig {

    /**
     * 配置JobDetail
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>FactoryBean可以简化Bean的实例化过程：
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>1.通过FactoryBean封装Bean的实例化过程。
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>2.将FactoryBean装配到Spring容器里。
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>3.将FactoryBean注入给其他的Bean。
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>4.该Bean得到的是FactoryBean所管理的对象实例。
     *
     * @return
     */
    //    @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     *
     * @param alphaJobDetail
     * @return
     */
    //    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }

    /**
     * 刷新帖子分数任务
     *
     * @return
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * @param postScoreRefreshJobDetail
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
