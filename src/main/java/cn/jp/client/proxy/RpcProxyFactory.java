package cn.jp.client.proxy;

import cn.jp.client.DataManage;
import cn.jp.client.FutureRpc;
import cn.jp.common.RequestRpc;

import javax.jws.Oneway;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

public class RpcProxyFactory<T>{
    private static RpcProxyFactory INSTANCE;

    private RpcProxyFactory(){}

    public static RpcProxyFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (RpcProxyFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RpcProxyFactory();
                }
            }
        }

        return INSTANCE;
    }

    public T getObject(Class<T> interfaceClass) {
        return getObjectJdk(interfaceClass);
    }

    private T getObjectCglib(Class<T> interfaceClass) {
        return null;
    }

    private T getObjectJdk(Class<T> interfaceClass) {
        T t = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                /* todo netty */
                if (Object.class == method.getDeclaringClass()) {
                    String name = method.getName();
                    if ("equals".equals(name)) {
                        return proxy == args[0];
                    } else if ("hashCode".equals(name)) {
                        return System.identityHashCode(proxy);
                    } else if ("toString".equals(name)) {
                        return proxy.getClass().getName() + "@" +
                                Integer.toHexString(System.identityHashCode(proxy)) +
                                ", with InvocationHandler " + this;
                    } else {
                        throw new IllegalStateException(String.valueOf(method));
                    }
                }

                RequestRpc requestRpc = new RequestRpc();
                requestRpc.setRequestId(UUID.randomUUID().toString());
                requestRpc.setClassName(interfaceClass.getName());
                requestRpc.setMethodName(method.getName());
                if (Objects.nonNull(args)) {
                    requestRpc.setParameters(args);
                    Class[] classTypes = new Class[args.length];
                    for (int i = 0; i < classTypes.length; i++) {
                        classTypes[i] = args[i].getClass();
                        requestRpc.setParameterTypes(classTypes);
                    }
                }


                FutureRpc futureRpc = DataManage.getINSTANCE().execute(requestRpc);

                return futureRpc.get();
            }
        });

        return t;
    }
}
