# 仿牛客网社区项目

![IDE](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-brightgreen.svg) ![Java](https://img.shields.io/badge/Java-1.8-blue.svg) ![Database](https://img.shields.io/badge/Database-MySQL-lightgrey.svg)

## 项目简介

一个仿照牛客网实现的讨论区，不仅实现了基本的注册、登录、发帖、评论、回复、私信等功能，同时使用前缀树实现敏感词过滤；使用 Redis 实现点赞与关注；使用 Kafka 处理发送评论、点赞和关注等系统通知；使用 Elasticsearch 实现全文搜索，关键词高亮显示；使用 wkhtmltopdf 生成长图和 PDF；实现网站 UV 和 DAU 统计；并将用户头像等信息存于七牛云服务器。

## 在线演示地址

~~https://community.turl.tech~~

## 测试账号

| 用户类型 | 用户名 | 密码   |
| -------- | ------ | ------ |
| 普通用户 | user   | 123456 |
| 版主     | banzhu | 123456 |
| 管理员   | admin  | 123456 |

## 功能列表

- [x] 邮件发送
- [x] 注册
- [x] 验证码
- [x] 登录
- [ ] 登录出错限制
- [ ] 第三方登录
- [x] 修改头像
- [x] 敏感词过滤
- [x] 发布帖子
- [ ] 我的帖子
- [x] 帖子详情
- [ ] 浏览量
- [x] 评论
- [x] 私信
- [x] 统一异常处理
- [x] 统一日志处理
- [x] 点赞
- [x] 关注
- [ ] 收藏
- [ ] 我的收藏
- [x] 系统通知
- [x] 搜索
- [x] 权限控制
- [x] 置顶、加精、删除
- [x] 网站统计
- [x] 定时执行任务计算热门帖子
- [x] 生成长图
- [x] 文件上传至七牛云
- [x] 监控

## 功能简介

- 使用 Redis 的 set 实现点赞，zset 实现关注，并使用 Redis 存储登录ticket和验证码，解决分布式 Session 问题，使用 Redis 的高级数据类型 HyperLogLog 统计 UV(Unique Visitor)，使用 Bitmap 统计 DAU(Daily Active User)。
- 使用 Kafka 处理发送评论、点赞、关注等系统通知、将新发布的帖子异步传输至Elasticsearch服务器，并使用事件进行封装，构建了强大的异步消息系统。
- 使用Elasticsearch做全局搜索，增加关键词高亮显示等功能。
- 热帖排行模块，使用本地缓存 Caffeine作为一级缓存和分布式缓存 Redis作为二级缓存构建多级缓存，避免了缓存雪崩，同时使用使用压测工具测试优化前后性能，将 QPS 提升了4倍(7.6/sec -> 33.5/sec)，大大提升了网站访问速度。并使用 Quartz 定时更新热帖排行。
- 使用 Spring Security 做权限控制，替代拦截器的拦截控制，并使用自己的认证方案替代 Security 认证流程，使权限认证和控制更加方便灵活。

## 技术栈

| 技术            | 链接                                                         | 版本           |
| --------------- | ------------------------------------------------------------ | -------------- |
| Spring Boot     | https://spring.io/projects/spring-boot                       | 2.4.3          |
| Spring          | https://spring.io/projects/spring-framework                  | 5.3.4          |
| Spring MVC      | https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web | 5.3.4          |
| MyBatis         | http://www.mybatis.org/mybatis-3                             | 3.5.1          |
| Redis           | https://redis.io/                                            | 5.0.3          |
| Kafka           | http://kafka.apache.org/                                     | 2.7.0          |
| Elasticsearch   | https://www.elastic.co/cn/elasticsearch/                     | 7.9.3          |
| Spring Security | https://spring.io/projects/spring-security                   | 5.4.5          |
| Spring Quartz   | https://www.baeldung.com/spring-quartz-schedule              | 2.3.2          |
| wkhtmltopdf     | https://wkhtmltopdf.org                                      | 0.12.6         |
| kaptcha         | https://github.com/penggle/kaptcha                           | 2.3.2          |
| Thymeleaf       | https://www.thymeleaf.org/                                   | 3.0.12.RELEASE |
| MySQL           | https://www.mysql.com/                                       | 5.7.17         |
| JDK             | https://www.oracle.com/java/technologies/javase-downloads.html | 1.8            |

## 系统架构

![image-20210331103427522](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331103427522.png)

![网站架构图](https://gitee.com/zhengguohuang/img/raw/master/img/%E7%BD%91%E7%AB%99%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

## 数据库初始化

```sql
create database community;
use community;
souuce /path/to/sql/init_schema.sql;
souuce /path/to/sql/init_data.sql;
souuce /path/to/sql/tables_mysql_innodb.sql;
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

## 运行效果展示

#### 发帖

![image-20210331105217867](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331105217867.png)

#### 帖子详情、评论

![image-20210331105241026](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331105241026.png)

#### 私信

![image-20210331105316546](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331105316546.png)

#### 系统通知

![image-20210331105333140](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331105333140.png)

#### 搜索

![image-20210331105404130](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331105404130.png)

#### 网站统计

![image-20210331105420047](https://gitee.com/zhengguohuang/img/raw/master/img/image-20210331105420047.png)

## 文档

https://easydoc.top/doc/70562937/SJuuLoYS/cI3Atvte

欢迎加群交流: 792364202

## 更新日志

* 2021-3-8 创建项目

