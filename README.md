# wechat-java
微信开发 Java SDK，支持包括公众号、小程序、微信支付、开放平台的后端开发
## 使用, 比如获取accessToken
```java
	@Test
    public void testAccessToken() {
        WechatPSInMemoryConfigImpl config = new WechatPSInMemoryConfigImpl();
        config.setAppId("你的appid");
        config.setSecret("你的secret");
        WechatPSServiceImpl psService = new WechatPSServiceImpl();
        psService.setWechatPSConfigStorage(config);
        String accessToken = psService.getAccessToken();
        Assert.assertNotNull(accessToken);
    }
```
> *需要重点关注获取accessToken，要考虑多线程线程安全和分布式
