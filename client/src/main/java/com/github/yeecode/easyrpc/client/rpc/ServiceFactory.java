package com.github.yeecode.easyrpc.client.rpc;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Service工厂
 * @param <T>
 */
public class ServiceFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    public ServiceFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * 在Service初始化时使用ServiceProxy的实现
     * @return
     */
    @Override
    public T getObject() {
        InvocationHandler handler = new ServiceProxy<>(interfaceType);
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[]{interfaceType}, handler);
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}