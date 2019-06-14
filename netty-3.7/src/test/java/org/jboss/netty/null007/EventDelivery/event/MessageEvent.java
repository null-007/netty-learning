package org.jboss.netty.null007.EventDelivery.event;

/**
 * @author huangqiang
 * @Title: MessageEvent
 * @Package org.jboss.netty.null007.EventDelivery.event
 * @Description:
 * @email 1308607536@qq.com
 * @date 2019/6/1414:11
 */
public class MessageEvent implements UpStreamChannelEvent {

    private String content;

    public MessageEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
