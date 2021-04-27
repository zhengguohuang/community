package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import tech.turl.community.util.RedisCellRateLimiter;

/**
 * @author zhengguohuang
 * @date 2021/04/27
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisCellTests {
    @Autowired private RedisCellRateLimiter redisCellRateLimiter;

    @Test
    public void testRateLimit() {
        System.out.println(redisCellRateLimiter.tryAcquire("rate:limit:post:1", 3, 3, 3600, 1));
    }
}
