package com.github.wujie.common.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wujie on 2019/1/12.
 */
@Data
@Builder
public class WechatJsapiSignature implements Serializable {

    private static final long serialVersionUID = -2665915866031943640L;

    private String appId;

    private String nonceStr;

    private long timestamp;

    private String url;

    private String signature;
}
