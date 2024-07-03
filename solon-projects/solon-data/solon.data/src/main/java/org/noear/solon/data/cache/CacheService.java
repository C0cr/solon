package org.noear.solon.data.cache;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * 缓存服务接口（用于支持@Cache相关注解）
 *
 * @author noear
 * @since 1.0
 * */
public interface CacheService {
    /**
     * 保存
     *
     * @param key     缓存键
     * @param obj     对象
     * @param seconds 秒数
     */
    void store(String key, Object obj, int seconds);


    /**
     * 移除
     *
     * @param key 缓存键
     */
    void remove(String key);

    /**
     * 获取
     *
     * @param key 缓存键
     * @deprecated 2.5
     */
    @Deprecated
    default Object get(String key){
        return get(key, Object.class);
    }

    /**
     * 获取
     *
     * @param key 缓存键
     */
    <T> T get(String key, Type type);

    /**
     * 获取
     *
     * @param key 缓存键
     */
    default <T> T get(String key, Class<T> type) {
        return get(key, (Type) type);
    }

    /**
     * 获取或者存储
     *
     * @since 1.7
     */
    default <T> T getOrStore(String key, Type type, int seconds, Supplier<T> supplier) {
        T obj = get(key, type);
        if (obj == null) {
            obj = supplier.get();
            store(key, obj, seconds);
        }

        return (T) obj;
    }
}
