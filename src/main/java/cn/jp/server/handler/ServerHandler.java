package cn.jp.server.handler;

import cn.jp.common.RequestRpc;
import cn.jp.common.ResponseRpc;
import cn.jp.server.producer.ApplicationManage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServerHandler extends SimpleChannelInboundHandler<RequestRpc> {
    public ServerHandler() {}
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestRpc msg) throws Exception {
        Object bean = ApplicationManage.getBean(msg.getClassName());
//        FastClass fastClass = FastClass.create(bean.getClass());
//        int methodIndex = fastClass.getIndex(msg.getMethodName(), msg.getParameterTypes());
//        Object result = fastClass.invoke(methodIndex, bean, msg.getParameters());
        Method method = bean.getClass().getDeclaredMethod(msg.getMethodName(), msg.getParameterTypes());
        Object result = method.invoke(bean, msg.getParameters());
        ResponseRpc responseRpc = new ResponseRpc();
        responseRpc.setRequestId(msg.getRequestId());
        responseRpc.setResult(result);
        System.out.println(result);
        ctx.channel().writeAndFlush(responseRpc).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("success");
            }
        });
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object obj = new UserTest();
        Class clazz = obj.getClass();
        Method method = clazz.getDeclaredMethod("getAll", null);
        Object result = method.invoke(obj, null);
        System.out.println(result);
    }
}
