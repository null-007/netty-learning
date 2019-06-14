package org.jboss.netty.null007.EventDelivery;

import org.jboss.netty.null007.EventDelivery.event.ChannelEvent;
import org.jboss.netty.null007.EventDelivery.event.ConnectEvent;
import org.jboss.netty.null007.EventDelivery.event.MessageEvent;
import org.jboss.netty.null007.EventDelivery.handler.*;

/**
 * @author huangqiang
 * @Title: PipelineTest
 * @Package org.jboss.netty.null007.EventDelivery
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:55
 */
public class PipelineTest {

    public static void main(String[] args) {
        // 创建 ChannelHandler
        ChannelHandler upHandlerFirst = new UpstreamChannelHandlerFirst();
        ChannelHandler upHandlerSecond = new UpstreamChannelHandlerSecond();
        ChannelHandler downHandlerFirst = new DownStreamChannelHandlerFirst();

        // 创建 ChannelPipeline
        ChannelPipeline channelPipeline = new ChannelPipeline(new Channel());
        channelPipeline.add(upHandlerFirst);
        channelPipeline.add(upHandlerSecond);
        channelPipeline.add(downHandlerFirst);
        // 创建 Connect 事件
        ChannelEvent connectEvent = new ConnectEvent("connect to the world, hello!");
        // 传递事件
        channelPipeline.upstreamSendEvent(connectEvent);


    }
}
