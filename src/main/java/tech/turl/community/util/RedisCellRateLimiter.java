package tech.turl.community.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengguohuang
 * @date 2021/04/27
 */
@Component
public class RedisCellRateLimiter implements CommunityConstant {
    @Autowired private RedisTemplate redisTemplate;
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n"
                    + "local init_burst = tonumber(ARGV[1])\n"
                    + "local max_burst = tonumber(ARGV[2])\n"
                    + "local period = tonumber(ARGV[3])\n"
                    + "local quota = ARGV[4]\n"
                    + "return redis.call('CL.THROTTLE',key,init_burst,max_burst,period,quota)";

    /**
     * 尝试申请资源
     *
     * @param key Redis key
     * @param initBurst 初始的容量
     * @param maxCapacity 漏桶最大容量
     * @param period 时间间隔
     * @param quote 目标资源数
     * @return 是否通过限流
     */
    public boolean tryAcquire(String key, int initBurst, int maxCapacity, int period, int quote) {
        List<String> keys = new ArrayList<>();
        keys.add(key);

        DefaultRedisScript<List<Long>> redisScript = new DefaultRedisScript(LUA_SCRIPT, List.class);
        Object result =
                redisTemplate.execute(redisScript, keys, initBurst, maxCapacity, period, quote);
        List<Long> res = (List<Long>) result;
        /** res.get(0) 通过拒绝 res.get(1) 漏斗容量 res.get(2) 剩余空间 res.get(3) 重试时间 res.get(4) 漏斗空间 */
        return res.get(0) == 0;
    }

    /**
     * 尝试申请资源
     *
     * @param
     * @return 是否通过限流
     */
    public boolean tryAcquire(String key, int initBurst, int maxCapacity, int period) {
        return this.tryAcquire(key, initBurst, maxCapacity, period, 1);
    }
}
