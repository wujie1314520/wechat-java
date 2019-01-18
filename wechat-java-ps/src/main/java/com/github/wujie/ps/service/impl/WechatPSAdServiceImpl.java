package com.github.wujie.ps.service.impl;

import com.github.wujie.common.bean.WechatAccessToken;
import com.github.wujie.common.bean.WechatError;
import com.github.wujie.common.constant.WechatType;
import com.github.wujie.common.exception.WechatErrorException;
import com.github.wujie.ps.service.WechatPSService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * Created by wujie on 2019/1/12.
 * 分布式
 */
@Slf4j
public class WechatPSAdServiceImpl extends WechatPSServiceImpl {

    //单机版
    @Override
    public String getAccessToken(boolean forceRefresh) throws WechatErrorException {
        Lock lock = getWechatPSConfigStorage().getAccessTokenLock();
        try {
            lock.lock();

            if (getWechatPSConfigStorage().isAccessTokenExpired() || forceRefresh) {
                if(getWechatPSConfigStorage().getAccessTokenRedisLock()) {
                    //拿到分布式锁，去刷新accessToken
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

                    //解锁
                    getWechatPSConfigStorage().unlock();
                }

            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }

        return getWechatPSConfigStorage().getAccessToken();
    }

}
