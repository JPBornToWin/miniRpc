package cn.jp.client;


import cn.jp.client.handler.ClientHandler;
import cn.jp.client.handler.ClientIdleHandler;
import cn.jp.client.handler.ClientMessageDecode;
import cn.jp.client.handler.ClientMessageEncode;
import cn.jp.server.handler.SplitFrame;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

import java.util.Objects;

/**
 * 一个client对应一个服务
 */
@Data
public class Client {
    private String address;

    private Channel channel;

    NioEventLoopGroup parentGroup;

    public Client(String address) {
        this.address = address;
        start();
    }

    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        parentGroup = new NioEventLoopGroup(1);
        bootstrap.group(parentGroup).
                channel(NioSocketChannel.class).
                handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ClientIdleHandler()).
                        addLast(new SplitFrame()).
                        addLast(new ClientMessageDecode()).
                        addLast(new ClientHandler()).
                        addLast(new ClientMessageEncode());
            }
        });
        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);
        try {
            ChannelFuture future = bootstrap.connect(ip, port).sync();
            this.channel = future.channel();
            channel.closeFuture().addListener(f -> parentGroup.shutdownGracefully());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (Objects.nonNull(channel)) {
            channel.close();
        }

        if (Objects.nonNull(parentGroup)) {
            parentGroup.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
