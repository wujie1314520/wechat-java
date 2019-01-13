package com.github.wujie.ps.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis分布式锁,采用双重防死锁
 */
@Slf4j
public class RedisLockUtil {
    private static RedisLockUtil mInstance = null;

    private final JedisPool jedisPool;

    private RedisLockUtil(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public static RedisLockUtil getInstance(JedisPool jedisPool) {
        if(mInstance == null) {
            synchronized (RedisLockUtil.class) {
                if(mInstance == null) {
                    mInstance = new RedisLockUtil(jedisPool);
                }
            }
        }
        return mInstance;
    }

    /**
     * 加锁
     * 为了应对高并发，采用双重防死锁
     * @param key
     * @param lockTimeout 超时时间
     * (key, value) value:当前时间 + 超时时间
     * @return
     */
    public boolean lock(String key, int lockTimeout) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long setnxResult = jedis.setnx(key, String.valueOf(System.currentTimeMillis() + lockTimeout));
            if (setnxResult != null && setnxResult.intValue() == 1) {
                //为了防止死锁，设置个存活时间
                jedis.expire(key, lockTimeout);
                return true;
            }
            //未获取到锁，判断时间戳，看是否可以重置并获取到锁
            String lockValueStr = jedis.get(key);
            if (StringUtils.isNotBlank(lockValueStr) && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {
                //旧锁已经过期
                //增强校验，重新赋值
                String getSetResult = jedis.getSet(key, String.valueOf(System.currentTimeMillis() + lockTimeout));
                //获取上一个锁的时间
                if (getSetResult == null || StringUtils.equals(lockValueStr, getSetResult)) {
                    //getSetResult == null，说明另外一个进程已经释放了锁
                    //lockValueStr与getSetResult相等，说明锁未被其他进程获取到
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("[redis异常]", e);
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 解锁
     * @param key
     */
    public void unlock(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            log.error("【redie分布式】解锁异常， {}", e);
        } finally {
            jedis.close();
        }
    }

//    public static void main(String[] args) {
//        //加锁
//        String productId = "123";
//        int TIMEOUT = 5000; //超时时间
//        long time = System.currentTimeMillis() + TIMEOUT;
//        RedisLock redisLock = new RedisLock();
//        if(!redisLock.lock(productId, String.valueOf(time))) {
//            throw new BusinessException(101, "哎哟喂，人太多了，换个姿势再试试");
//        }
//
    //todo 业务处理进行一系列操作...
//
//        //解锁
//        redisLock.unlock(productId, String.valueOf(time));
//    }
}
