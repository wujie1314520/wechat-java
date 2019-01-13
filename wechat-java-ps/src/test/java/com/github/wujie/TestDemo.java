package com.github.wujie;

import com.github.wujie.ps.service.impl.WechatPSInMemoryConfigImpl;
import com.github.wujie.ps.service.impl.WechatPSServiceImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by wujie on 2019/1/12.
 *
 */
public class TestDemo {

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

//    @Test
//    public void testAccessTokenInRedis() {
//        //基于redis，实现分布式锁
//        WechatPSInMemoryConfigImpl config = new WechatPSInRedisConfigImpl(jedisPool);
//        config.setAppId("你的appid");
//        config.setSecret("你的secret");
//        WechatPSServiceImpl psService = new WechatPSServiceImpl();
//        psService.setWechatPSConfigStorage(config);
//        String accessToken = psService.getAccessToken();
//        Assert.assertNotNull(accessToken);
//    }

}
