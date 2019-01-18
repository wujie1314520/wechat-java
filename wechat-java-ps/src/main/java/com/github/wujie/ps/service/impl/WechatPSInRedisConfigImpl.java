package com.github.wujie.ps.service.impl;

import com.github.wujie.ps.utils.RedisLock;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by wujie on 2019/1/13.
 * 基于Redis的微信配置provider. 调用者可以根据自己的实际情况进行重写
 */
@Slf4j
public class WechatPSInRedisConfigImpl extends WechatPSInMemoryConfigImpl {

    protected static final String ACCESS_TOKEN_KEY = "wechat:accesstoken:";
    protected static final String LOCK_ACCESS_TOKEN_KEY = "wechat:accesstokenlock:";
    protected static final int LOCKTIMEOUT = 1; //分布式锁的超时时间暂停为1秒，具体视业务进行调整

    //使用连接池保证线程安全.
    //单节点
    protected final JedisPool jedisPool;

    private String accessTokenKey;
    private String accessTokenLockKey;
    private RedisLock lock;

    public WechatPSInRedisConfigImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

   //该项目支持多个公众号，所以每个公众号需要生成独有的存储key来区分
    @Override
    public void setAppId(String appId) {
        super.setAppId(appId);
        this.accessTokenKey = ACCESS_TOKEN_KEY.concat(appId);
        this.accessTokenLockKey = LOCK_ACCESS_TOKEN_KEY.concat(appId);
    }

    @Override
    public String getAccessToken() {
        String accessToken = null;
        Jedis jedis = null;
        //注意：这里不要乱用jdk7的try-with-resources，try-with-resources会在结束后自动调用close方法
        //但前提是:括号里的资源实现类必须实现AutoCloseable或Closeable接口
        try {
            jedis = this.jedisPool.getResource();
            accessToken = jedis.get(this.accessTokenKey);
        } catch (Exception e) {
           log.error("[redis异常]读取失败", e);
        } finally {
            jedis.close();
        }
        return accessToken;
    }

    @Override
    public boolean isAccessTokenExpired() {
        Long result = null;
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            result = jedis.ttl(accessTokenKey);
        } catch (Exception e) {
            log.error("[redis异常]读取失败", e);
        } finally {
            jedis.close();
        }
        return result != null && result < 0;
    }

    public boolean getAccessTokenRedisLock() {
        //分布式锁
        this.lock = RedisLock.getInstance(this.jedisPool);
        return this.lock.lock(this.accessTokenLockKey, LOCKTIMEOUT);
    }

    public void unlock() {
        this.lock.unlock(this.accessTokenLockKey); //释放锁
    }

    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        //分布式锁
//        RedisLock lock = RedisLock.getInstance(this.jedisPool);
//        //抢到锁才能更改
//        if(lock.lock(this.accessTokenLockKey, LOCKTIMEOUT)) {
//            Jedis jedis = null;
//            try {
//                jedis = this.jedisPool.getResource();
//                jedis.setex(this.accessTokenKey, expiresInSeconds - 200, accessToken);
//                lock.unlock(this.accessTokenLockKey); //释放锁
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                jedis.close();
//            }
//        }

        Jedis jedis = null;
        try {
                jedis = this.jedisPool.getResource();
                jedis.setex(this.accessTokenKey, expiresInSeconds - 200, accessToken);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jedis.close();
            }
    }

    @Override
    public void expireAccessToken() {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.expire(this.accessTokenKey, 0);
        }
    }

}
