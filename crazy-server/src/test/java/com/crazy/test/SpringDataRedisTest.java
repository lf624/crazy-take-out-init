package com.crazy.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisTemplate() {
        System.out.println(redisTemplate);
        // 五种类型的数据操作对象
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
    }

    @Test
    public void testString() {
        // set get setex setnx
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name", "小明");
        String value = (String) valueOperations.get("name");
        System.out.println(value);
        // 将 key 的过期时间设为 3 分钟
        valueOperations.set("code", "1234",3, TimeUnit.MINUTES);
        valueOperations.setIfAbsent("lock", "1");
        valueOperations.setIfAbsent("lock", "2");
    }

    @Test
    public void testHash() {
        //hset hget hdel hkeys hvals
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.put("100", "name", "tom");
        hashOperations.put("100", "age", "24");

        String name = hashOperations.get("100", "name");
        System.out.println(name);

        Set<String> keys = hashOperations.keys("100");
        System.out.println(keys);

        List<String> values = hashOperations.values("100");
        System.out.println(values);

        hashOperations.delete("100", "age");
    }

    @Test
    public void testList() {
        //lpush lrange rpop llen
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();

        listOperations.leftPushAll("mylist", "a", "b", "c");
        listOperations.leftPush("mylist", "d");

        List<Object> list = listOperations.range("mylist", 0, -1);
        System.out.println(list); // [d, c, b, a]

        listOperations.rightPop("mylist");

        Long size = listOperations.size("mylist");
        System.out.println(size);
    }

    @Test
    public void testSet() {
        //sadd smembers scard sinter sunion srem
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();

        setOperations.add("set1", "a", "b", "c", "d");
        setOperations.add("set2", "a", "b", "x", "y");

        Set<Object> members = setOperations.members("set1");
        assertEquals(members, Set.of("a", "b", "c", "d"));

        Long size = setOperations.size("set1");
        assertEquals(size, 4);

        Set<Object> intersect = setOperations.intersect("set1", "set2");
        assertEquals(intersect, Set.of("a", "b"));

        Set<Object> union = setOperations.union("set1", "set2");
        assertEquals(union, Set.of("a", "b", "c", "d", "x", "y"));

        setOperations.remove("set1", "a", "b");
        assertEquals(setOperations.members("set1"), Set.of("c", "d"));
    }

    @Test
    public void testZSet() {
        // zadd zrange zincrby zrem
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.add("zset1", "a", 10);
        zSetOperations.add("zset1", "b", 12);
        zSetOperations.add("zset1", "c", 9);

        Set<Object> zset = zSetOperations.range("zset1", 0, -1);
        System.out.println(zset); // [c, a, b]

        zSetOperations.incrementScore("zset1", "c", 10);
        System.out.println(zSetOperations.range("zset1", 0, -1)); // [a, b, c]

        zSetOperations.remove("zset1", "a", "b");
    }

    @Test
    public void testCommon() {
        // keys exists type del
        Set<String> keys = redisTemplate.keys("*");
        System.out.println(keys);

        Boolean name = redisTemplate.hasKey("name");
        Boolean set1 = redisTemplate.hasKey("set1");

        for(String key : keys) {
            DataType type = redisTemplate.type(key);
            System.out.println(type.name());
        }

        redisTemplate.delete("mylist");
    }
}
