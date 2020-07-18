package cn.jp.client.handler;

import cn.jp.common.RequestRpc;
import cn.jp.protocol.ProtocolConstants;
import cn.jp.protocol.SerializationUtils;
import cn.jp.protocol.SerializeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientMessageEncode extends MessageToByteEncoder<RequestRpc> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RequestRpc msg, ByteBuf out) throws Exception {
        out.writeInt(ProtocolConstants.MagicNumber);
        out.writeByte(ProtocolConstants.version);
        out.writeByte(SerializeEnum.ProtoBuf.type);
        byte[] bytes = SerializationUtils.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
