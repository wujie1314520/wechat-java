package com.github.wujie.common.utils.xml;

import com.thoughtworks.xstream.converters.basic.StringConverter;

/**
 * Created by wujie on 2019/1/11.
 * 从 <ToUserName>< ![CDATA[toUser] ]></ToUserName> 提取 “toUser”
 */
public class XStreamCDataConverter extends StringConverter {

    @Override
    public String toString(Object obj) {
        return "<![CDATA[" + super.toString(obj) + "]]>";
    }

}
