package com.github.wujie.common.exception;

import com.github.wujie.common.bean.WechatError;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by wujie on 2019/1/12.
 */
@Getter
@NoArgsConstructor
public class WechatErrorException extends RuntimeException {

    private Integer code;

    public WechatErrorException(WechatError wxError) {
        super(wxError.getErrmsg());
        this.code = wxError.getErrcode();
    }

    public WechatErrorException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
