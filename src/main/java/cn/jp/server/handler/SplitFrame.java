package cn.jp.server.handler;

import cn.jp.protocol.ProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class SplitFrame extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 6;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public SplitFrame() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        byteBuf.markReaderIndex();
        if (byteBuf.readInt() != ProtocolConstants.MagicNumber) {
            ctx.channel().close();
            return null;
        }

        byteBuf.resetReaderIndex();

        return byteBuf;
    }
}
