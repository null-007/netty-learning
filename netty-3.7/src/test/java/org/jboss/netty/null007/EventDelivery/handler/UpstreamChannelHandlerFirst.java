package org.jboss.netty.null007.EventDelivery.handler;

import org.jboss.netty.null007.EventDelivery.ChannelHandlerContextNode;
import org.jboss.netty.null007.EventDelivery.event.ChannelEvent;
import org.jboss.netty.null007.EventDelivery.event.MessageEvent;

/**
 * @author huangqiang
 * @Title: ChannelHandlerOne
 * @Package org.jboss.netty.null007.EventDelivery.handler
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:18
 */
public class UpstreamChannelHandlerFirst extends UpstreamChannelHandler {


    void onConnect(ChannelHandlerContextNode ctxNode, ChannelEvent event) {
        System.out.println("连接事件被" + this.getClass().getSimpleName() + "处理了: 事件内容是: " + event.getContent());
        // 向下一个节点转发
        ctxNode.upstreamSendEvent(event);

    }

    void onReceiveMessage(ChannelHandlerContextNode ctxNode, ChannelEvent event) {
        System.out.println("消息接受事件被" + this.getClass().getSimpleName() + "处理了: 事件内容是: " + event.getContent());
        // 向下一个节点转发
        ctxNode.upstreamSendEvent(event);
    }
}
