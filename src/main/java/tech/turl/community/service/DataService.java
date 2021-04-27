package tech.turl.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.turl.community.util.RedisKeyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
@Service
public class DataService {
    @Autowired private RedisTemplate redisTemplate;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    /**
     * 统计UV，将指定ip计入UV
     *
     * @param ip
     */
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    /**
     * 统计指定日期范围内的UV
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public long calculateUV(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 整理该日期范围内的Key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            String key = RedisKeyUtil.getUvKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }
        // 合并这些数据
        String redisKey = RedisKeyUtil.getUvKey(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        // 统计数据
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    /**
     * 统计DAU，将指定用户计入DAU
     *
     * @param
     */
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    /**
     * 统计指定日期范围内的DAU
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public long calculateDAU(Date startDate, Date endDate) {
        // 整理该日期范围内的Key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            String key = RedisKeyUtil.getDauKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE, 1);
        }
        // 合并这些数据
        String redisKey = RedisKeyUtil.getDauKey(df.format(startDate), df.format(endDate));
        return (long)
                redisTemplate.execute(
                        (RedisCallback)
                                redisConnection -> {
                                    redisConnection.bitOp(
                                            RedisStringCommands.BitOperation.OR,
                                            redisKey.getBytes(),
                                            keyList.toArray(new byte[0][0]));
                                    return redisConnection.bitCount(redisKey.getBytes());
                                });
    }
}
