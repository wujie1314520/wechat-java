package com.github.wujie.ps.service.impl;

import com.github.wujie.common.bean.WechatAccessToken;
import com.github.wujie.ps.service.WechatPSConfigStorage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created wujie hp on 2019/1/12.
 * 基于内存的微信配置provider，只适用于单体
 * 在集群或者分布式生产环境中，需要将这些配置持久化
 */

public class WechatPSInMemoryConfigImpl implements WechatPSConfigStorage {

    protected volatile String appId;
    protected volatile String secret;
    protected volatile String accessToken;
    protected volatile long expiresTime; //过期时间:获取到accessToken的当前时间 + 返回json中的过期时间
    protected volatile String oauth2redirectUri;
    protected volatile String jsapiTicket;
    protected volatile long jsapiTicketExpiresTime;

    protected Lock accessTokenLock = new ReentrantLock(true); //公平锁
    protected Lock jsapiTicketLock = new ReentrantLock(true);

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public Lock getAccessTokenLock() {
        return this.accessTokenLock;
    }

    @Override
    public boolean getAccessTokenRedisLock() {
        return false;
    }

    @Override
    public void unlock() {
    }

    @Override
    public boolean isAccessTokenExpired() {
        return System.currentTimeMillis() > this.expiresTime;
    }

    @Override
    public void expireAccessToken() {
        this.expiresTime = 0;
    }

    //一定要保证线程安全
    @Override
    public synchronized void updateAccessToken(WechatAccessToken accessToken) {
        updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    //一定要保证线程安全
    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        this.accessToken = accessToken;
        //这里考虑性能，预留个200秒，即在accessToken真实过期200秒之前就让它过期
        this.expiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000L;
    }

    public void setJsapiTicket(String jsapiTicket) {
        this.jsapiTicket = jsapiTicket;
    }

    @Override
    public String getJsapiTicket() {
        return this.jsapiTicket;
    }

    @Override
    public Lock getJsapiTicketLock() {
        return this.jsapiTicketLock;
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return System.currentTimeMillis() > this.jsapiTicketExpiresTime;
    }

    @Override
    public void expireJsapiTicket() {
        this.jsapiTicketExpiresTime = 0;
    }

    //一定要保证线程安全
    @Override
    public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        this.jsapiTicket = jsapiTicket;
        // 预留200秒的时间
        this.jsapiTicketExpiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000L;
    }

    @Override
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public long getExpiresTime() {
        return this.expiresTime;
    }

    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;
    }
}
