package com.github.wujie.ps.service;

/**
 * Created by wujie on 2019/1/11.
 * 微信公众号网页授权API的Service
 * 注意：
 * 通过网页授权获得的access_token，只能获取到对应的微信用户信息，与微信用户是一对一关系；而普通的access_token在有效期内可以使用，可以获取所有用户信息。
 * 普通access_token每天获取最多次数为2000次，而网页授权的access_token获取次数没有限制。两者有效时间都是7200s。
 */
public interface WechatPSOauthService {

    /**
     * 用code换取oauth2的access token
     */
    String OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 刷新oauth2的access token
     */
    String OAUTH2_REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    /**
     * 用oauth2获取用户信息
     */
    String OAUTH2_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=%s";

    /**
     * 验证oauth2的access token是否有效
     */
    String OAUTH2_VALIDATE_TOKEN_URL = "https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s";

    /**
     * oauth2授权的url连接
     */
    String CONNECT_OAUTH2_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&connect_redirect=1#wechat_redirect";
}
