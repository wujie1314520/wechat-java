package com.github.wujie.common.constant;

/**
 * Created by wujie on 2019/1/11.
 * 微信开发所使用到的常量类
 */
public interface WechatConsts {
    /**
     * 微信推送过来的消息的类型，和发送给微信xml格式消息的消息类型.
     */
    interface XmlMsgType {
        String EVENT = "event";
        String TEXT = "text";
        String IMAGE = "image";
        String VOICE = "voice";
        String SHORTVIDEO = "shortvideo";
        String VIDEO = "video";
        String NEWS = "news";
        String MUSIC = "music";
        String LOCATION = "location";
        String LINK = "link";
    }
}
