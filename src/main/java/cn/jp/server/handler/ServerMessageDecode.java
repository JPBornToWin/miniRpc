package cn.jp.server.handler;

import cn.jp.common.RequestRpc;
import cn.jp.protocol.ProtocolPackage;
import cn.jp.protocol.SerializationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ServerMessageDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        ProtocolPackage protocolPackage = getProtocolPackage(byteBuf);
        int len = byteBuf.readInt();
        if (len == 0) {
            System.out.println("心跳");
            return;
        }
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        RequestRpc requestRpc = SerializationUtils.deserialize(RequestRpc.class, bytes);
        list.add(requestRpc);
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
