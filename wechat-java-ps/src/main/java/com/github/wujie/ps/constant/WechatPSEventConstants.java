package com.github.wujie.ps.constant;

/**
 * Created by wujie on 2019/1/11.
 * 事件消息类型
 * 公有部分
 * <xml>
 *      <ToUserName>< ![CDATA[toUser] ]></ToUserName>
 *     <FromUserName>< ![CDATA[FromUser] ]></FromUserName>
 *     <CreateTime>123456789</CreateTime>
 *     <MsgType>< ![CDATA[event] ]></MsgType>
 * </xml>
 */
public interface WechatPSEventConstants {
    //消息类型，event
    String MSGTYPE_EVENT = "event";

    /**
     * 扫描带参数二维码事件
     * <xml>
     *     <Event>< ![CDATA[subscribe] ]></Event>
     * </xml>
     */
    interface EventSubscribe {
        String SUBSCRIBE = "subscribe"; //关注
        String UNSUBSCRIBE = "unsubscribe"; //取消关注
    }

    /**
     * 扫描带参数二维码事件
     *
     * 1.用户未关注时，进行关注后的事件推送:
     * <xml>
     *     <Event>< ![CDATA[subscribe] ]></Event>
     *     <EventKey>< ![CDATA[qrscene_123123] ]></EventKey>
     *     <Ticket>< ![CDATA[TICKET] ]></Ticket>
     * </xml>
     *  Event    事件类型，subscribe
     *  EventKey 事件KEY值，qrscene_为前缀，后面为二维码的参数值
     *  Ticket	 二维码的ticket，可用来换取二维码图片
     *
     * 2.用户已关注时的事件推送：
     * <xml>
     *     <Event>< ![CDATA[SCAN] ]></Event>
     *     <EventKey>< ![CDATA[SCENE_VALUE] ]></EventKey>
     *     <Ticket>< ![CDATA[TICKET] ]></Ticket>
     * </xml>
     *  Event    事件类型，SCAN
     *  EventKey 事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
     *  Ticket	 二维码的ticket，可用来换取二维码图片
     */

    /**
     * 上报地理位置事件
     * 用户同意上报地理位置后，每次进入公众号会话时，都会在进入时上报地理位置，或在进入会话后每5秒上报一次地理位置，
     * 公众号可以在公众平台网站中修改以上设置。上报地理位置时，微信会将上报地理位置事件推送到开发者填写的URL。
     *  <xml>
     *      <Event>< ![CDATA[LOCATION] ]></Event>
     *      <Latitude>23.137466</Latitude>
     *      <Longitude>113.352425</Longitude>
     *      <Precision>119.385040</Precision> 地理位置精度
     *  </xml>
     */
    String EVENT_LOCATION = "LOCATION";

    /**
     * 自定义菜单事件
     * 用户点击自定义菜单后，微信会把点击事件推送给开发者，请注意，点击菜单弹出子菜单，不会产生上报。
     *
     * 1.点击菜单拉取消息时的事件推送
     * <xml>
     *     <Event>< ![CDATA[CLICK] ]></Event>
     *     <EventKey>< ![CDATA[EVENTKEY] ]></EventKey>
     * </xml>
     *   Event	事件类型，CLICK
     *   EventKey	事件KEY值，与自定义菜单接口中KEY值对应
     *
     * 2.点击菜单跳转链接时的事件推送
     * <xml>
     *     <Event>< ![CDATA[VIEW] ]></Event>
     *     <EventKey>< ![CDATA[www.qq.com] ]></EventKey>
     * </xml>
     * Event	事件类型，VIEW
     * EventKey	事件KEY值，设置的跳转URL
     */
    interface EventMenu {
        String CLICK = "CLICK"; //点击菜单拉取消息时的事件推送
        String VIEW = "VIEW"; // 点击菜单跳转链接时的事件推送
    }
}
