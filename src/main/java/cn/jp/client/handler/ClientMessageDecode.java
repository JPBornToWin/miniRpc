package cn.jp.client.handler;

import cn.jp.common.RequestRpc;
import cn.jp.common.ResponseRpc;
import cn.jp.protocol.ProtocolPackage;
import cn.jp.protocol.SerializationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ClientMessageDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ProtocolPackage protocolPackage = getProtocolPackage(in);
        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        ResponseRpc responseRpc = SerializationUtils.deserialize(ResponseRpc.class, bytes);
        out.add(responseRpc);
    }

    private ProtocolPackage getProtocolPackage(ByteBuf byteBuf) {
        ProtocolPackage protocolPackage = new ProtocolPackage();
        int magicNumber = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializeAlgorithm = byteBuf.readByte();
        protocolPackage.setMagicNumber(magicNumber);
        protocolPackage.setVersion(version);
        protocolPackage.setSerializeAlgorithm(serializeAlgorithm);

        return protocolPackage;
    }
}
