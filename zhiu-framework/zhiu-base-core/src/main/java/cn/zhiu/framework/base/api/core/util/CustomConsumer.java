package cn.zhiu.framework.base.api.core.util;

@FunctionalInterface
public interface CustomConsumer<T, U, R> {

    void accept(T t, U u, R r);
}
