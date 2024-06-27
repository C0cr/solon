package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.Result;

import java.util.Collection;
import java.util.List;

/**
 * 路由表
 *
 * @author noear
 * @since 1.7
 */
public interface RoutingTable<T> {
    /**
     * 添加路由记录
     *
     * @param routing 路由
     */
    void add(Routing<T> routing);

    /**
     * 添加路由记录
     *
     * @param routing 路由
     * @param index   索引位置
     */
    void add(int index, Routing<T> routing);

    /**
     * 移除路由记录
     *
     * @param pathPrefix 路径前缀
     */
    void remove(String pathPrefix);

    /**
     * 移除路由记录
     *
     * @param controllerClz 控制器类
     */
    void remove(Class<?> controllerClz);

    /**
     * 数量
     */
    int count();

    /**
     * 获取所有的路由记录
     */
    Collection<Routing<T>> getAll();

    /**
     * 获取路径的路由记录
     *
     * @param path 路径
     * @since 2.6
     */
    Collection<Routing<T>> getBy(String path);

    /**
     * 获取控制器的路由记录
     *
     * @param controllerClz 控制器类
     * @since 2.8
     */
    Collection<Routing<T>> getBy(Class<?> controllerClz);

    /**
     * 区配一个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    T matchOne(String path, MethodType method);

    /**
     * 区配一个目标并给出状态
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     * @since 2.5
     */
    Result<T> matchOneAndStatus(String path, MethodType method);


    /**
     * 区配多个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一批区配的目标
     * @since 2.5
     */
    List<T> matchMore(String path, MethodType method);


    /**
     * 清空
     */
    void clear();
}
