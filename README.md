# 仿牛客网社区项目

## 项目简介

一个仿照牛客网实现的讨论区，不仅实现了基本的注册、登录、发帖、评论、回复、私信、点赞、关注等功能，同时使用前缀树实现敏感词过滤；使用 Kafka 处理发送评论、点赞和关注等系统通知；使用 Elasticsearch 实现全局搜索，增加关键词高亮显示等功能；使用 wkhtmltopdf 生成长图和 PDF，实现网站 UV 和 DAU 统计；并将用户头像等信息存于七牛云服务器。

## 在线演示地址

https://community.turl.tech

## 功能列表

- [x] 邮件发送
- [x] 注册
- [x] 验证码
- [x] 登录
- [x] 修改头像
- [x] 敏感词过滤
- [x] 发布帖子
- [x] 帖子详情
- [x] 评论
- [x] 私信
- [x] 统一异常处理
- [x] 统一日志处理
- [x] 点赞
- [x] 关注
- [x] 系统通知
- [ ] 搜索
- [ ] 权限控制
- [ ] 置顶、加精、删除
- [ ] 网站统计
- [ ] 任务执行和调度
- [ ] 热帖

## 功能简介

- 使用 Redis 的 set 实现点赞，zset 实现关注，并使用 Redis 存储登录ticket和验证码，解决分布式 Session 问题，使用 Redis 的高级数据类型 HyperLogLog 统计 UV(Unique Visitor)，使用 Bitmap 统计 DAU(Daily Active User)。
- 使用 Kafka 处理发送评论、点赞和关注等系统通知，并使用事件进行封装，构建了强大的异步消息系统。
- 使用Elasticsearch做全局搜索，并通过事件封装，增加关键词高亮显示等功能。
- 热帖排行模块，使用分布式缓存 Redis 和本地缓存 Caffeine 作为多级缓存，避免了缓存雪崩，将 QPS 提升了20倍，大大提升了网站访问速度。并使用 Quartz 定时更新热帖排行。
- 使用 Spring Security 做权限控制，替代拦截器的拦截控制，并使用自己的认证方案替代 Security 认证流程，使权限认证和控制更加方便灵活。

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

