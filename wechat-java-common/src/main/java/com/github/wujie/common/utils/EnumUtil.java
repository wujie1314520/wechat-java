package com.github.wujie.common.utils;


import com.github.wujie.common.enums.ICode;

/**
 * 查找指定code的枚举工具类
 */
public class EnumUtil {

    public static <T extends ICode> T getByCode(Integer code, Class<T> enumClass) {
        for (T codeEnum: enumClass.getEnumConstants()) {
            if (code.equals(codeEnum.getCode())) {
                return codeEnum;
            }
        }
        return null;
    }
}
