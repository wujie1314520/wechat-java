package com.github.wujie.ps.message;

import com.github.wujie.common.utils.xml.XStreamCDataConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Created by wujie on 2019/1/11.
 **<pre>
 * 微信推送过来的消息，xml格式.
 * 相关微信开发文档：
 * <a href="http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140454&token=&lang=zh_CN">接收事件推送</a>
 * <a href="http://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140453&token=&lang=zh_CN">接收普通消息</a>
 * 推送XML数据包示例：
 *  <xml>
 *      <ToUserName>< ![CDATA[toUser] ]></ToUserName>
 *      <FromUserName>< ![CDATA[FromUser] ]></FromUserName>
 *      <CreateTime>123456789</CreateTime>
 *      <MsgType>< ![CDATA[event] ]></MsgType>
 *      <Event>< ![CDATA[subscribe] ]></Event>
 *      <Event>< ![CDATA[subscribe] ]></Event>
 *     <EventKey>< ![CDATA[qrscene_123123] ]></EventKey>
 *     <Ticket>< ![CDATA[TICKET] ]></Ticket>
 *      ...
 *  </xml>
 * 注意：
 *  微信服务器在五秒内收不到响应会断掉连接，并且重新发起请求，总共重试三次。
 *  假如服务器无法保证在五秒内处理并回复，可以直接回复空串，微信服务器不会对此作任何处理，并且不会发起重试。
 * </pre>
 */
@Data
@Slf4j
@XStreamAlias("xml")
public class WechatPSXmlMessage implements Serializable{

    private static final long serialVersionUID = 5120711552674591966L;

    /*************************************公共部分开始*******************************************/

    //开发者微信号
    @XStreamAlias("ToUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String toUser;

    //发送方帐号（一个OpenID）
    @XStreamAlias("FromUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String fromUser;

    //消息创建时间 （整型）
    @XStreamAlias("CreateTime")
    private Long createTime;

    //消息类型
    @XStreamAlias("MsgType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String msgType;

    //事件类型
    @XStreamAlias("Event")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String event;

    /*************************************公共部分结束*******************************************/

    //事件KEY值
    @XStreamAlias("EventKey")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String eventKey;

    //二维码的ticket，可用来换取二维码图片
    @XStreamAlias("Ticket")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String ticket;

    //维度
    @XStreamAlias("Latitude")
    private Double latitude;

    //经度
    @XStreamAlias("Longitude")
    private Double longitude;

    //精度
    @XStreamAlias("Precision")
    private Double precision;
}
