package cn.jp.client.handler;

import cn.jp.protocol.ProtocolConstants;
import cn.jp.protocol.SerializeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

public class ClientIdleHandler extends ChannelInboundHandlerAdapter {
    private static final int HEARTBEAT_INTERVAL = 8;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        heartBit(ctx);
        super.channelActive(ctx);
    }

    public void heartBit(ChannelHandlerContext ctx) {
        ctx.executor().scheduleAtFixedRate(() -> {
            if (ctx.channel().isActive()) {
                ByteBuf byteBuf = Unpooled.buffer();
                byteBuf.writeInt(ProtocolConstants.MagicNumber);
                byteBuf.writeByte(ProtocolConstants.version);
                byteBuf.writeByte(SerializeEnum.ProtoBuf.type);
                byteBuf.writeInt(0);
                ctx.writeAndFlush(byteBuf);
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }
}
