# wechat-java
微信开发 Java SDK，支持包括公众号、小程序、微信支付、开放平台的后端开发
## 使用, 比如获取accessToken
```java
	@Test
    public void testAccessTokenInMemory() {
        //基于内存
        WechatPSInMemoryConfigImpl config = new WechatPSInMemoryConfigImpl();
        config.setAppId("你的appid");
        config.setSecret("你的secret");
        WechatPSServiceImpl psService = new WechatPSServiceImpl();
        psService.setWechatPSConfigStorage(config);
        String accessToken = psService.getAccessToken();
        Assert.assertNotNull(accessToken);
    }

    @Test
    public void testAccessTokenInRedis() {
        //基于redis，实现分布式锁
        ...
        WechatPSInMemoryConfigImpl config = new WechatPSInRedisConfigImpl(jedisPool);
        config.setAppId("你的appid");
        config.setSecret("你的secret");
        WechatPSServiceImpl psService = new WechatPSServiceImpl();
        psService.setWechatPSConfigStorage(config);
        String accessToken = psService.getAccessToken();
        Assert.assertNotNull(accessToken);
    }
```
> *需要重点关注获取accessToken，要考虑多线程线程安全和分布式
> *基于Redis的微信配置provider,需要根据自己实现情况重写
```java
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

    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        //分布式锁
        RedisLockUtil lock = RedisLockUtil.getInstance(this.jedisPool);
        //抢到锁才能更改
        if(lock.lock(this.accessTokenLockKey, LOCKTIMEOUT)) {
            Jedis jedis = null;
            try {
                jedis = this.jedisPool.getResource();
                jedis.setex(this.accessTokenKey, expiresInSeconds - 200, accessToken);
                lock.unlock(this.accessTokenLockKey); //释放锁
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jedis.close();
            }
        }
    }

    @Override
    public void expireAccessToken() {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.expire(this.accessTokenKey, 0);
        }
    }

}
```
## redis分布式锁,采用双重防死锁
```java
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
```

