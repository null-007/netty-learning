package org.jboss.netty.null007.EventDelivery;

import org.jboss.netty.null007.EventDelivery.event.ChannelEvent;
import org.jboss.netty.null007.EventDelivery.handler.ChannelHandler;
import org.jboss.netty.null007.EventDelivery.handler.DownstreamChannelHandler;
import org.jboss.netty.null007.EventDelivery.handler.UpstreamChannelHandler;

/**
 * @author huangqiang
 * @Title: ChannelHandlerContextNode
 * @Package org.jboss.netty.null007.EventDelivery
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:16
 */
public class ChannelHandlerContextNode {

    private ChannelHandler handler;

    private ChannelHandlerContextNode pre;
    private ChannelHandlerContextNode next;

    public ChannelHandlerContextNode getPre() {
        return pre;
    }

    public void setPre(ChannelHandlerContextNode pre) {
        this.pre = pre;
    }

    public void setNext(ChannelHandlerContextNode next) {
        this.next = next;
    }

    public ChannelHandlerContextNode getNext() {
        return next;
    }

    private Boolean isUpstreamHandler;
    private Boolean isDownStreamHanlder;

    private ChannelPipeline channelPipeline;

    public ChannelHandlerContextNode(ChannelPipeline channelPipeline, ChannelHandler handler) {
        isUpstreamHandler = handler instanceof UpstreamChannelHandler;
        isDownStreamHanlder = handler instanceof DownstreamChannelHandler;
        this.channelPipeline = channelPipeline;
        this.handler = handler;
    }

    public Boolean isUpstreamHandler() {
        return isUpstreamHandler;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    public Boolean isDownStreamHanlder() {
        return isDownStreamHanlder;
    }


    public void upstreamSendEvent(ChannelEvent event) {
        ChannelHandlerContextNode next = this.channelPipeline.getActualUpstreamHandlerNode(this.next);
        this.channelPipeline.upstreamSendEvent(next, event);
    }


}
