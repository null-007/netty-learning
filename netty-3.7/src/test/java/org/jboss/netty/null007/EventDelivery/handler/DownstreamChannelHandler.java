package org.jboss.netty.null007.EventDelivery.handler;

import org.jboss.netty.null007.EventDelivery.ChannelHandlerContextNode;
import org.jboss.netty.null007.EventDelivery.event.*;

/**
 * @author huangqiang
 * @Title: DownstreamChannelHandler
 * @Package org.jboss.netty.null007.EventDelivery.handler
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:14
 */
public abstract class DownstreamChannelHandler implements ChannelHandler {

    public void downstreamSendEvent(ChannelHandlerContextNode ctxNode, ChannelEvent event) {
        if (event instanceof DownStreamChannelEvent) {
            if (event instanceof MessageEvent) {
                onSendMessage(ctxNode, event);
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
    abstract void onSendMessage(ChannelHandlerContextNode ctxNode, ChannelEvent event);
}
