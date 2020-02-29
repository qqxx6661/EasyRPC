package com.github.yeecode.easyrpc.client.rpc;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态代理：实现InvocationHandler接口
 * @param <T>
 */
public class ServiceProxy<T> implements InvocationHandler {

    private T target;

    public ServiceProxy(T target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 看Service类有没有指定@RemoteClass名称
        RemoteClass remoteClass = method.getDeclaringClass().getAnnotation(RemoteClass.class);
        if (remoteClass == null) {
            throw new Exception("远程类标志未指定");
        }

        // 获取被调用方法的参数类型
        List<String> argTypeList = new ArrayList<>();
        if (args != null) {
            for (Object obj : args) {
                argTypeList.add(obj.getClass().getName());
            }
        }

        // 将远程类的名称，方法名，参数类型和参数值进行远程调用
        String argTypes = JSON.toJSONString(argTypeList);
        String argValues = JSON.toJSONString(args);
        Result result = HttpUtil.callRemoteService(remoteClass.value(), method.getName(), argTypes, argValues);

        if (result.isSuccess()) {
            return JSON.parseObject(result.getResultValue(), Class.forName(result.getResultType()));
        } else {
            throw new Exception("远程调用异常：" + result.getMessage());
        }
    }
}