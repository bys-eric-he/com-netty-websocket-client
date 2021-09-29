package com.nettty.websocket.init;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public abstract class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 处理输出数据
     *
     * @return
     */
    protected abstract ChannelHandler getClientHandler();

    /**
     * 设置子通道也就是SocketChannel的处理器， 其内部是实际业务开发的"主战场"
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new IdleStateHandler(30, 0, 5));
        p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0));
        p.addLast(getClientHandler());
    }
}
