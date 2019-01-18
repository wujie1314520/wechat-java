package com.github.wujie.ps.service.impl;

import com.github.wujie.common.bean.WechatAccessToken;
import com.github.wujie.common.bean.WechatError;
import com.github.wujie.common.bean.WechatJsapiSignature;
import com.github.wujie.common.constant.WechatType;
import com.github.wujie.common.exception.WechatErrorException;
import com.github.wujie.ps.service.WechatPSConfigStorage;
import com.github.wujie.ps.service.WechatPSService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by wujie on 2019/1/12.
 * 微信公众号服务
 */
@Slf4j
public class WechatPSServiceImpl implements WechatPSService {

    private OkHttpClient httpClient;
    private WechatPSConfigStorage wechatPSConfigStorage;

    public WechatPSConfigStorage getWechatPSConfigStorage() {
        return wechatPSConfigStorage;
    }

    public void setWechatPSConfigStorage(WechatPSConfigStorage wechatPSConfigStorage) {
        this.wechatPSConfigStorage = wechatPSConfigStorage;
        initHttp();
    }

    @Override
    public boolean checkSignature(String timestamp, String nonce, String signature) {
        return false;
    }

    @Override
    public String getAccessToken() throws WechatErrorException {
        return getAccessToken(false);
    }

    //单机版
    @Override
    public String getAccessToken(boolean forceRefresh) throws WechatErrorException {
        Lock lock = getWechatPSConfigStorage().getAccessTokenLock();
        try {
            lock.lock();

            if (getWechatPSConfigStorage().isAccessTokenExpired() || forceRefresh) {

                String url = String.format(WechatPSService.GET_ACCESS_TOKEN_URL,
                        getWechatPSConfigStorage().getAppId(), getWechatPSConfigStorage().getSecret());

                Request request = new Request.Builder().url(url).get().build();
                Response response = getRequestHttpClient().newCall(request).execute();
                String resultContent = response.body().string();
                //不知道返回类型,有可能是错误，有可能是正常返回
                WechatError error = WechatError.fromJson(resultContent, WechatType.WECHAT_PUBLIC_SUBSCRPTION);
                if (error != null && error.getErrcode() != 0) {
                    //0：是成功 非0是失败
                    throw new WechatErrorException(error);
                }
                WechatAccessToken accessToken = WechatAccessToken.fromJson(resultContent);
                getWechatPSConfigStorage().updateAccessToken(accessToken.getAccessToken(),
                        accessToken.getExpiresIn());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return getWechatPSConfigStorage().getAccessToken();
    }

    @Override
    public String getJsapiTicket() throws WechatErrorException {
        return null;
    }

    @Override
    public String getJsapiTicket(boolean forceRefresh) throws WechatErrorException {
        return null;
    }

    @Override
    public WechatJsapiSignature createJsapiSignature(String url) throws WechatErrorException {
        return null;
    }

    private static final int DEFAULT_TIMEOUT = 30; //默认超时时间30秒

    public OkHttpClient getRequestHttpClient() {
        return httpClient;
    }

    private void initHttp() {
        log.debug("initHttp...");
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .build();
    }

}
