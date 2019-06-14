package org.jboss.netty.null007.EventDelivery.handler;

import org.jboss.netty.null007.EventDelivery.ChannelHandlerContextNode;
import org.jboss.netty.null007.EventDelivery.event.ChannelEvent;

/**
 * @author huangqiang
 * @Title: DownStreamChannelHandlerFirst
 * @Package org.jboss.netty.null007.EventDelivery.handler
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1415:45
 */
public class DownStreamChannelHandlerFirst extends DownstreamChannelHandler {

    void onConnect(ChannelHandlerContextNode ctxNode, ChannelEvent event) {
        System.out.println("连接事件被" + this.getClass().getSimpleName() + "处理了: 事件内容是: " + event.getContent());
        ctxNode.upstreamSendEvent(event);

    }

    void onSendMessage(ChannelHandlerContextNode ctxNode, ChannelEvent event) {
        System.out.println("消息发送事件被" + this.getClass().getSimpleName() + "处理了: 事件内容是: " + event.getContent());
        // 向下一个节点转发
        ctxNode.upstreamSendEvent(event);
    }
}
