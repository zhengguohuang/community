package tech.turl.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {}
