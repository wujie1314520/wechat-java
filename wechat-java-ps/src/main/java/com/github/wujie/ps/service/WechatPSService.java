package com.github.wujie.ps.service;

import com.github.wujie.common.bean.WechatJsapiSignature;
import com.github.wujie.common.exception.WechatErrorException;

/**
 * Created by wujie on 2019/1/11.
 * 微信公众号API的Service
 */
public interface WechatPSService {
    /**
     * 获取微信服务器IP地址
     */
    String GET_CALLBACK_IP_URL = "https://api.weixin.qq.com/cgi-bin/getcallbackip";

    /**
     * 获取access_token
     */
    String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 获得jsapi_ticket
     */
    String GET_JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi";

    /**
     * 长链接转短链接接口
     * 生成二维码的时候比较有用
     */
    String SHORTURL_API_URL = "https://api.weixin.qq.com/cgi-bin/shorturl";

    /**
     * 语义查询接口
     * 通过语义接口，接收用户发送的自然语言请求，让系统理解用户的说话内容。
     */
    String SEMANTIC_SEMPROXY_SEARCH_URL = "https://api.weixin.qq.com/semantic/semproxy/search";

    /**
     * 第三方使用网站应用授权登录的url
     */
    String QRCONNECT_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    /**
     * 获取公众号的自动回复规则
     */
    String GET_CURRENT_AUTOREPLY_INFO_URL = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info";

    /**
     * 公众号调用或第三方平台帮公众号调用对公众号的所有api调用（包括第三方帮其调用）次数进行清零
     */
    String CLEAR_QUOTA_URL = "https://api.weixin.qq.com/cgi-bin/clear_quota";

    /**
     * <pre>
     * 验证消息的确来自微信服务器
     * 详情请见: http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421135319&token=&lang=zh_CN
     * </pre>
     */
    boolean checkSignature(String timestamp, String nonce, String signature);

    /**
     * 获取access_token, 不强制刷新access_token
     * @see #getAccessToken(boolean)
     */
    String getAccessToken() throws WechatErrorException;

    /**
     * <pre>
     * 获取access_token，本方法线程安全
     * 且在多线程同时刷新时只刷新一次，避免超出2000次/日的调用次数上限
     * 另：本service的所有方法都会在access_token过期时调用此方法
     * 程序员在非必要情况下尽量不要主动调用此方法
     * 详情请见: http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183&token=&lang=zh_CN
     * </pre>
     * @param forceRefresh 强制刷新
     */
    String getAccessToken(boolean forceRefresh) throws WechatErrorException;

    /**
     * 获得jsapi_ticket,不强制刷新jsapi_ticket
     * @see #getJsapiTicket(boolean)
     */
    String getJsapiTicket() throws WechatErrorException;

    /**
     * <pre>
     * 获得jsapi_ticket
     * 获得时会检查jsapiToken是否过期，如果过期了，那么就刷新一下，否则就什么都不干
     * 详情请见：http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141115&token=&lang=zh_CN
     * </pre>
     * @param forceRefresh 强制刷新
     */
    String getJsapiTicket(boolean forceRefresh) throws WechatErrorException;

    /**
     * <pre>
     * 创建调用jsapi时所需要的签名
     * 详情请见：http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141115&token=&lang=zh_CN
     * </pre>
     */
    WechatJsapiSignature createJsapiSignature(String url) throws WechatErrorException;

}
