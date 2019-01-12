package com.github.wujie.common.bean;

import com.github.wujie.common.utils.JsonUtils;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by wujei on 2019/1/12.
 * accessToken
 * 注意：
 * 通过网页授权获得的access_token，只能获取到对应的微信用户信息，与微信用户是一对一关系；而普通的access_token在有效期内可以使用，可以获取所有用户信息。
 * 普通access_token每天获取最多次数为2000次，而网页授权的access_token获取次数没有限制。两者有效时间都是7200s。
 */
@Data
public class WechatAccessToken implements Serializable {

    private static final long serialVersionUID = -3792169063437921001L;

    private String accessToken;

    private int expiresIn = -1;

    public static WechatAccessToken fromJson(String json) throws IOException {
        return JsonUtils.toObject(json, WechatAccessToken.class);
    }
}
