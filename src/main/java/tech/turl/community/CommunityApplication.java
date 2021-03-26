package tech.turl.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author zhengguohuang
 * @date 2021/03/26
 */
@SpringBootApplication
public class CommunityApplication {
    @PostConstruct
    public void init() {
        // 解决netty启动冲突问题
        // see Netty4Utils
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        // 启动Tomcat，创建容器
        SpringApplication.run(CommunityApplication.class, args);
    }

}
