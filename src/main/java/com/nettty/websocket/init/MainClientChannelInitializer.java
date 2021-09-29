package com.nettty.websocket.init;

import com.nettty.websocket.client.ClientStart;
import com.nettty.websocket.handler.ClientHandler;
import io.netty.channel.ChannelHandler;

public class MainClientChannelInitializer extends ClientChannelInitializer{
    /**
     * 处理输出数据
     *
     * @return
     */
    @Override
    protected ChannelHandler getClientHandler() {
        return new ClientHandler(new ClientStart());
    }
}
