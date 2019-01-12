package com.github.wujie.common.bean;

import com.github.wujie.common.constant.WechatType;
import com.github.wujie.common.enums.WechatPSErrorsEnum;
import com.github.wujie.common.utils.EnumUtil;
import com.github.wujie.common.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by wujie on 2019/1/11.
 * 微信返回错误码格式：{"errcode": 40013,"errmsg": "invalid appid"}
 */
@Getter
@Setter
public class WechatError implements Serializable {

    private static final long serialVersionUID = 468893770621702220L;

    private Integer errcode;
    private String errmsg;

    public static WechatError fromJson(String json) throws IOException {
        return fromJson(json, null);
    }

    public static WechatError fromJson(String json, WechatType type){
        final WechatError wxError = JsonUtils.toObject(json, WechatError.class);
        if(null == wxError) return null;

        if (null == type) {
            return wxError;
        }
        //拿到“英文的错误解释”去“错误返回码”中寻找到相应的中文msg
        //如果是微信公众号
        if(type == WechatType.WECHAT_PUBLIC_SUBSCRPTION) {
            WechatPSErrorsEnum errorEnum = EnumUtil.getByCode(wxError.getErrcode(), WechatPSErrorsEnum.class);
            if(null != errorEnum) {
                wxError.setErrmsg(errorEnum.getMsg());
            }
        }
        return wxError;
    }
}
