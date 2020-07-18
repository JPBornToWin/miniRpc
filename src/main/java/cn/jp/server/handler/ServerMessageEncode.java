package cn.jp.server.handler;

import cn.jp.common.ResponseRpc;
import cn.jp.protocol.ProtocolConstants;
import cn.jp.protocol.SerializationUtils;
import cn.jp.protocol.SerializeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ServerMessageEncode extends MessageToByteEncoder<ResponseRpc> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseRpc responseRpc, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(ProtocolConstants.MagicNumber);
        byteBuf.writeByte(ProtocolConstants.version);
        byteBuf.writeByte(SerializeEnum.ProtoBuf.type);
        byte[] bytes = SerializationUtils.serialize(responseRpc);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
