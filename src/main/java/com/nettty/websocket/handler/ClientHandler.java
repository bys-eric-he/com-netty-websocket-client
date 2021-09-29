package com.nettty.websocket.handler;

import com.nettty.websocket.client.ClientStart;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ClientHeartbeatHandler {

    private ClientStart client;

    public ClientHandler(ClientStart client) {
        super("客户端client-邓宝宝");
        this.client = client;
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        System.out.println(name + " 收到消息内容: " + content);
    }

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx);
    }

    /**
     * 重写 channelInactive 方法. 当 TCP 连接断开时, 会回调 channelInactive 方法,
     * 因此我们在这个方法中调用 client.doConnect() 来进行重连.
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        client.doConnect();
    }
}