package com.nettty.websocket.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ClientHeartbeatHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CUSTOM_MSG = 3;
    protected String name;
    private int heartbeatCount = 0;

    public ClientHeartbeatHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, ByteBuf byteBuf) throws Exception {
        if (byteBuf.getByte(4) == PING_MSG) {
            sendPongMsg(context);
        } else if (byteBuf.getByte(4) == PONG_MSG) {
            log.info(name + " 收到 pong 数据包 来自 " + context.channel().remoteAddress());
        } else {
            handleData(context, byteBuf);
        }
    }

    protected void sendPingMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PING_MSG);
        buf.retain();
        context.writeAndFlush(buf);
        heartbeatCount++;
        log.info(name + " 发送 ping 数据包到 " + context.channel().remoteAddress() + ", 次数: " + heartbeatCount);
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        ByteBuf buf = context.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PONG_MSG);
        context.channel().writeAndFlush(buf);
        heartbeatCount++;
        System.out.println(name + " 发送 pong 数据包到 " + context.channel().remoteAddress() + ", 次数: " + heartbeatCount);
    }

    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("---客户端：" + ctx.channel().remoteAddress() + " 变为活跃状态（连接到了远程主机）, 可以接受和发送数据!---");
        // 向服务端发送数据
        String msg = "hello,我是最帅的 Netty Client!!!!";
        ByteBuf buf = ctx.channel().alloc().buffer(5 + msg.getBytes().length);
        buf.writeInt(5 + msg.getBytes().length);
        buf.writeByte(ClientHeartbeatHandler.CUSTOM_MSG);
        buf.writeBytes(msg.getBytes());
        ctx.channel().writeAndFlush(buf);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("--- 客户端：" + ctx.channel().remoteAddress() + " 处于非活跃状态, 没有连接到远程主机!---");
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---READER_IDLE 读空闲---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE 写空闲---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE 读和写空闲---");
    }
}
