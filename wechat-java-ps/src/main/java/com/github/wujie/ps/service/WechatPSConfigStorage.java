package com.github.wujie.ps.service;

import com.github.wujie.common.bean.WechatAccessToken;

import java.util.concurrent.locks.Lock;

/**
 * Created by wujie on 2019/1/12.
 * 微信客户端配置存储
 * accessToken一般有效期为2个小时，一定要注意多线程并发时accessToken过期问题，以及集群或者分布式accessToken过期问题
 */
public interface WechatPSConfigStorage {
    
    String getAccessToken();

    Lock getAccessTokenLock();

    boolean getAccessTokenRedisLock();

    void unlock();

    boolean isAccessTokenExpired();

    /**
     * 强制将access token过期掉
     */
    void expireAccessToken();

    /**
     * 应该是线程安全的
     * @param accessToken 要更新的WechatAccessToken对象
     */
    void updateAccessToken(WechatAccessToken accessToken);

    /**
     * 应该是线程安全的
     * @param accessToken      新的accessToken值
     * @param expiresInSeconds 过期时间，以秒为单位
     */
    void updateAccessToken(String accessToken, int expiresInSeconds);

    String getJsapiTicket();

    Lock getJsapiTicketLock();

    boolean isJsapiTicketExpired();

    /**
     * 强制将jsapi ticket过期掉
     */
    void expireJsapiTicket();

    /**
     * 应该是线程安全的
     * @param jsapiTicket      新的jsapi ticket值
     * @param expiresInSeconds 过期时间，以秒为单位
     */
    void updateJsapiTicket(String jsapiTicket, int expiresInSeconds);

    String getAppId();

    String getSecret();

    long getExpiresTime();
}
