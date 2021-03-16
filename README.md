# 开源社区

## 在线演示地址

http://community.turl.tech

## 功能列表

- [x] 邮件发送
- [x] 注册
- [x] 验证码
- [x] 登录
- [x] 修改头像
- [ ] 讨论区
- [ ] 敏感词过滤
- [ ] 权限控制
- [ ] 邮件激活
- [ ] 点赞
- [ ] 关注
- [ ] 私信
- [ ] 搜索
- [ ] 网站统计

## 技术栈

| 技术            | 链接                                                         |
| --------------- | ------------------------------------------------------------ |
| Spring Boot     | https://spring.io/projects/spring-boot                       |
| Spring          | https://spring.io/projects/spring-framework                  |
| Spring MVC      | https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web |
| MyBatis         | http://www.mybatis.org/mybatis-3                             |
| Redis           | https://redis.io/                                            |
| Kafka           | http://kafka.apache.org/                                     |
| Elasticsearch   | https://www.elastic.co/cn/elasticsearch/                     |
| Spring Security | https://spring.io/projects/spring-security                   |
| Spring Actuator | https://docs.spring.io/spring-boot/docs/2.4.3/reference/html/production-ready-features.html#production-ready |

## 数据库初始化

```sql
create database community;
use community;
souuce /path/to/sql/init_schema.sql;
souuce /path/to/sql/init_data.sql;
```



## 运行

1. 安装JDK，Maven

2. 克隆代码到本地

   ```bash
   git clone https://github.com/zhengguohuang/community.git
   ```

3. 运行打包命令

   ```bash
   mvn package
   ```

4. 运行项目

   ```bash
   java -jar xxx.jar
   ```

5. 访问项目

   ```
   http://localhost:8080
   ```

## 更新日志

* 2021-3-8 创建项目

