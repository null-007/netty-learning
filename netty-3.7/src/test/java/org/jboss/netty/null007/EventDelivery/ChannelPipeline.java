package org.jboss.netty.null007.EventDelivery;

import org.jboss.netty.null007.EventDelivery.event.ChannelEvent;
import org.jboss.netty.null007.EventDelivery.handler.ChannelHandler;
import org.jboss.netty.null007.EventDelivery.handler.UpstreamChannelHandler;

/**
 * @author huangqiang
 * @Title: ChannelPipeline
 * @Package org.jboss.netty.null007.EventDelivery
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:29
 */
public class ChannelPipeline {

    private Channel channel;

    private ChannelHandlerContextNode head;
    private ChannelHandlerContextNode tail;

    public ChannelPipeline(Channel channel) {
        this.channel = channel;
    }

    /**
     * 事件起始
     * @param event
     */
    public void upstreamSendEvent(ChannelEvent event) {
        ChannelHandlerContextNode contextNode = getActualUpstreamHandlerNode(this.head);
        this.upstreamSendEvent(contextNode, event);
    }

    public void upstreamSendEvent(ChannelHandlerContextNode contextNode, ChannelEvent event) {
        if (contextNode == null) {
            return;
        }
        UpstreamChannelHandler handler = (UpstreamChannelHandler) contextNode.getHandler();
        handler.upstreamSendEvent(contextNode, event);

    }

    public ChannelHandlerContextNode getActualUpstreamHandlerNode(ChannelHandlerContextNode head) {
        ChannelHandlerContextNode contextNode = head;
        while (contextNode != null) {
            if (contextNode.getHandler() instanceof UpstreamChannelHandler) {
                return contextNode;
            }
            contextNode = contextNode.getNext();
        }
        return contextNode;
    }

    /**
     * 顺序插入
     * @param handler
     */
    public void add(ChannelHandler handler) {
        ChannelHandlerContextNode contextNode = new ChannelHandlerContextNode(this, handler);
        if (head == null) {
            head = contextNode;
            tail = contextNode;
        } else if (head == tail) {
            head.setNext(contextNode);
            contextNode.setPre(head);
            tail = contextNode;
        } else {
            contextNode.setNext(tail);
            contextNode.setPre(tail.getPre());
            tail.getPre().setNext(contextNode);
            tail.setPre(contextNode);
        }
    }
}
