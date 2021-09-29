package com.nettty.websocket;

import com.nettty.websocket.client.ClientStart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NettyClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(NettyClientApplication.class, args);

        try {
            ClientStart client = new ClientStart();
            client.start();
            client.sendData();
            log.info("*******Netty Client启动完成*******");
        } catch (Exception exception) {
            log.error("启动客户端异常, 异常内容:" + exception);
        }
    }
}
