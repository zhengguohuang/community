package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * @author zhengguohuang
 * @date 2021/03/21
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
  @Autowired private RedisTemplate redisTemplate;

  @Test
  public void testString() {
    String redisKey = "test:count";
    redisTemplate.opsForValue().set(redisKey, 1);
    System.out.println(redisTemplate.opsForValue().get(redisKey));
    System.out.println(redisTemplate.opsForValue().increment(redisKey));
    System.out.println(redisTemplate.opsForValue().increment(redisKey));
  }

  @Test
  public void testHashes() {
    String redisKey = "test:user";
    redisTemplate.opsForHash().put(redisKey, "id", 1);
    redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");

    System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
    System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
  }

  @Test
  public void testKeys() {
    redisTemplate.delete("test:user");
    System.out.println(redisTemplate.hasKey("test:user"));
    redisTemplate.expire("user:student", 10, TimeUnit.SECONDS);
  }

  @Test
  public void testList() {
    String redisKey = "test:ids";

    redisTemplate.opsForList().leftPush(redisKey, 101);
    redisTemplate.opsForList().leftPush(redisKey, 102);
    redisTemplate.opsForList().leftPush(redisKey, 103);

    System.out.println(redisTemplate.opsForList().size(redisKey));
    System.out.println(redisTemplate.opsForList().index(redisKey, 0));
    System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

    System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    System.out.println(redisTemplate.opsForList().leftPop(redisKey));
  }

  @Test
  public void testSets() {
    String redisKey = "test:teachers";
    redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞");
    System.out.println(redisTemplate.opsForSet().size(redisKey));
    System.out.println(redisTemplate.opsForSet().pop(redisKey));
    System.out.println(redisTemplate.opsForSet().members(redisKey));
  }

  @Test
  public void testSortedSet() {
    String redisKey = "test:students";
    redisTemplate.opsForZSet().add(redisKey, "唐僧1", 80);
    redisTemplate.opsForZSet().add(redisKey, "唐僧2", 60);
    redisTemplate.opsForZSet().add(redisKey, "唐僧3", 90);
    redisTemplate.opsForZSet().add(redisKey, "唐僧4", 70);

    System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
    System.out.println(redisTemplate.opsForZSet().score(redisKey, "唐僧2"));
    System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 1));
    System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "唐僧4"));
  }

  /** 多次访问同一个key */
  @Test
  public void testBoundOperations() {
    String redisKey = "test:count";
    BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
    operations.increment();
    operations.increment();
    operations.increment();
    operations.increment();
    operations.increment();
    System.out.println(operations.get());
  }

  @Test
  public void testTransactional() {
    Object obj =
        redisTemplate.execute(
            new SessionCallback() {
              @Override
              public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey, "zhangsan");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec();
              }
            });
    System.out.println(obj);
  }

  /** 统计20万个重复数据的独立总数 */
  @Test
  public void testHyperLogLog() {
    String redisKey = "test:hll:01";
    for (int i = 1; i <= 100; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey, i);
    }
    for (int i = 1; i <= 100; i++) {
      int random = (int) (Math.random() * 100 + 1);
      redisTemplate.opsForHyperLogLog().add(redisKey, random);
    }
    long size = redisTemplate.opsForHyperLogLog().size(redisKey);
    System.out.println(size);
  }

  /** 将3组数据合并再统计合并的重复数据的独立总数 */
  @Test
  public void testHyperLogLogUnion() {
    String redisKey2 = "test:hll:02";
    for (int i = 1; i <= 10000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey2, i);
    }
    String redisKey3 = "test:hll:03";
    for (int i = 5001; i <= 15000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey3, i);
    }
    String redisKey4 = "test:hll:04";
    for (int i = 10001; i <= 20000; i++) {
      redisTemplate.opsForHyperLogLog().add(redisKey4, i);
    }

    String unionKey = "test:hll:union";
    redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);
    long size = redisTemplate.opsForHyperLogLog().size(unionKey);
    System.out.println(size);
  }

  @Test
  public void testBitMap() {
    String redisKey = "test:bm:01";
    // 设置
    redisTemplate.opsForValue().setBit(redisKey, 1, true);
    redisTemplate.opsForValue().setBit(redisKey, 4, true);
    redisTemplate.opsForValue().setBit(redisKey, 7, true);
    // 查询
    System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
    System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
    System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

    // 统计
    Object obj =
        redisTemplate.execute(
            (RedisCallback) redisConnection -> redisConnection.bitCount(redisKey.getBytes()));
    System.out.println(obj);
  }

  @Test
  public void testBitMapOperation() {
    String redisKey2 = "test:bm:02";
    redisTemplate.opsForValue().setBit(redisKey2, 0, true);
    redisTemplate.opsForValue().setBit(redisKey2, 1, true);
    redisTemplate.opsForValue().setBit(redisKey2, 2, true);
    String redisKey3 = "test:bm:03";
    redisTemplate.opsForValue().setBit(redisKey3, 2, true);
    redisTemplate.opsForValue().setBit(redisKey3, 3, true);
    redisTemplate.opsForValue().setBit(redisKey3, 4, true);
    String redisKey4 = "test:bm:04";
    redisTemplate.opsForValue().setBit(redisKey4, 4, true);
    redisTemplate.opsForValue().setBit(redisKey4, 5, true);
    redisTemplate.opsForValue().setBit(redisKey4, 6, true);

    String redisKey = "test:bm:or";
    Object obj =
        redisTemplate.execute(
            (RedisCallback)
                redisConnection -> {
                  redisConnection.bitOp(
                      RedisStringCommands.BitOperation.OR,
                      redisKey.getBytes(),
                      redisKey2.getBytes(),
                      redisKey3.getBytes(),
                      redisKey4.getBytes());
                  return redisConnection.bitCount(redisKey.getBytes());
                });
    System.out.println(obj);
  }
}
