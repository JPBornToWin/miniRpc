package cn.jp.server;

import cn.jp.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 所有服务均由该 Server 提供
 */
public class Server implements InitializingBean {
    private EventLoopGroup parentGroup;

    private EventLoopGroup childGroup;

    private Integer port;

    private Integer childThreadNum;

    private ServerBootstrap serverBootstrap;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private String zkServer;

    public Server (Integer port, Integer childThreadNum, String zkServer) {
        this.port = port;
        this.childThreadNum = childThreadNum;
        this.zkServer = zkServer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public void start() throws InterruptedException {
        serverBootstrap = new ServerBootstrap();
        parentGroup = new NioEventLoopGroup(1);
        childGroup = new NioEventLoopGroup(childThreadNum);
        serverBootstrap.group(parentGroup, childGroup).
                channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).
                childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().
                                addLast(new ServerIdleStateHandler()).
                                addLast(new SplitFrame()).
                                addLast(new ServerMessageDecode()).
                                addLast(new ServerHandler()).
                                addLast(new ServerMessageEncode());
                    }
                });
        ChannelFuture future = serverBootstrap.bind(port).sync();

        System.out.println("==== server start ====");

        future.channel().
                closeFuture().
                addListener((ChannelFuture channelFuture) -> shutdown());

    }

    public void shutdown() {
        if (Objects.nonNull(parentGroup)) {
            parentGroup.shutdownGracefully();
        }

        if (Objects.nonNull(childGroup)) {
            childGroup.shutdownGracefully();
        }

    }
}
