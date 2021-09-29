package com.nettty.websocket.client;

import com.nettty.websocket.handler.ClientHeartbeatHandler;
import com.nettty.websocket.init.MainClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientStart {

    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;

    /**
     * 发送数据
     *
     * @throws Exception
     */
    public void sendData() throws Exception {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10000; i++) {
            if (channel != null && channel.isActive()) {
                String content = "客户端的消息-> 我是每2秒自动发送的消息！消息序列号：" + i;
                ByteBuf buf = channel.alloc().buffer(5 + content.getBytes().length);
                buf.writeInt(5 + content.getBytes().length);
                buf.writeByte(ClientHeartbeatHandler.CUSTOM_MSG);
                buf.writeBytes(content.getBytes());
                channel.writeAndFlush(buf);
            }

            Thread.sleep(random.nextInt(20000));
        }
    }

    /**
     * 启动客户端实例
     */
    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new MainClientChannelInitializer());
            doConnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 连接
     */
    public void doConnect() {
        try {
            if (channel != null && channel.isActive()) {
                return;
            }

            ChannelFuture future = bootstrap.connect("127.0.0.1", 2222);

            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture futureListener) throws Exception {
                    if (futureListener.isSuccess()) {
                        channel = futureListener.channel();
                        log.info("-->成功连接到Netty Server服务器!");
                    } else {
                        log.warn("-->连接到服务器失败, 10s 秒后将进行重试!");

                        futureListener.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect();
                            }
                        }, 10, TimeUnit.SECONDS);
                    }
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
