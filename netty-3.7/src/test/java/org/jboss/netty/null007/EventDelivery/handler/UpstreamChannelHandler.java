package org.jboss.netty.null007.EventDelivery.handler;

import org.jboss.netty.null007.EventDelivery.ChannelHandlerContextNode;
import org.jboss.netty.null007.EventDelivery.event.ChannelEvent;
import org.jboss.netty.null007.EventDelivery.event.ConnectEvent;
import org.jboss.netty.null007.EventDelivery.event.MessageEvent;
import org.jboss.netty.null007.EventDelivery.event.UpStreamChannelEvent;

/**
 * @author huangqiang
 * @Title: UpstreamChannelHandler
 * @Package org.jboss.netty.null007.EventDelivery.handler
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:14
 */
public abstract class UpstreamChannelHandler implements ChannelHandler {

    public void upstreamSendEvent(ChannelHandlerContextNode ctxNode, ChannelEvent event) {
        if (event instanceof UpStreamChannelEvent) {
            if (event instanceof MessageEvent) {
                onReceiveMessage(ctxNode, event);
            } else if (event instanceof ConnectEvent) {
                onConnect(ctxNode, event);
            } else {
                //  向下一个节点转发
                ctxNode.upstreamSendEvent(event);
            }
        } else {
            // 向下一个节点转发
            ctxNode.upstreamSendEvent(event);
        }

    }
    // 事件处理方法
    abstract void onConnect(ChannelHandlerContextNode ctxNode, ChannelEvent event);
    // 事件处理方法
    abstract void onReceiveMessage(ChannelHandlerContextNode ctxNode, ChannelEvent event);
}
