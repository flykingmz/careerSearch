package com.tencent.wxcloudrun.interceptor;

public class BaseContext {
    // 使用 ThreadLocal 存储上下文信息
    private static final ThreadLocal<Long> contextHolder = new ThreadLocal<>();

    // 将上下文信息存储到当前线程
    public static void setCurrentId(Long userId) {
        contextHolder.set(userId);
    }

    // 从当前线程检索上下文信息
    public static Long getCurrentId() {
        return contextHolder.get();
    }

    // 从当前线程移除上下文信息
    public static void remove() {
        contextHolder.remove();
    }
}
