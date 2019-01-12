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
    public void testAccessToken() {
        WechatPSInMemoryConfigImpl config = new WechatPSInMemoryConfigImpl();
        config.setAppId("wx8284b9bb1c56b21e");
        config.setSecret("49c69fb1bdf3a91d9a011bbd464893f9");
        WechatPSServiceImpl psService = new WechatPSServiceImpl();
        psService.setWechatPSConfigStorage(config);
        String accessToken = psService.getAccessToken();
        Assert.assertNotNull(accessToken);
    }

}
