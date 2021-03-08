package tech.turl.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		// 启动Tomcat，创建容器
		SpringApplication.run(CommunityApplication.class, args);
	}

}
