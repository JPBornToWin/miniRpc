package cn.jp.client.handler;

import cn.jp.client.DataManage;
import cn.jp.common.ResponseRpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ResponseRpc> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseRpc msg) throws Exception {
        DataManage dataManage = DataManage.getINSTANCE();
        dataManage.getRequestRpcMap().remove(msg.getRequestId());
        if (dataManage.getFutureRpcMap().containsKey(msg.getRequestId())) {
            System.out.println(msg.getResult());
            dataManage.getFutureRpcMap().get(msg.getRequestId()).done(msg.getResult());
            dataManage.getFutureRpcMap().remove(msg.getRequestId());
        }
    }
}
