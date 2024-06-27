package org.noear.solon.core.route;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.Result;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 路由表默认实现
 *
 * @author noear
 * @since 1.0
 * */
public class RoutingTableDefault<T> implements RoutingTable<T> {
    private List<Routing<T>> table = new ArrayList<>();


    /**
     * 添加路由记录
     *
     * @param routing 路由
     */
    @Override
    public void add(Routing<T> routing) {
        table.add(routing);
    }

    /**
     * 添加路由记录
     *
     * @param routing 路由
     * @param index   索引位置
     */
    @Override
    public void add(int index, Routing<T> routing) {
        table.add(index, routing);
    }

    @Override
    public void remove(String pathPrefix) {
        table.removeIf(l -> l.path().startsWith(pathPrefix));
    }

    @Override
    public void remove(Class<?> controllerClz) {
        table.removeIf(l -> {
            if (l.target() instanceof Action) {
                Action a = (Action) l.target();
                if (a.controller().clz().equals(controllerClz)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public int count() {
        return table.size();
    }

    @Override
    public Collection<Routing<T>> getAll() {
        return Collections.unmodifiableList(table);
    }

    @Override
    public Collection<Routing<T>> getBy(String path) {
        return table.stream()
                .filter(l -> l.test(path))
                .sorted(Comparator.comparingInt(l -> l.index()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Routing<T>> getBy(Class<?> controllerClz) {
        return table.stream()
                .filter(l -> {
                    if (l.target() instanceof Action) {
                        Action a = (Action) l.target();
                        if (a.controller().clz().equals(controllerClz)) {
                            return true;
                        }
                    }
                    return false;
                })
                .sorted(Comparator.comparingInt(l -> l.index()))
                .collect(Collectors.toList());
    }

    /**
     * 区配一个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    public T matchOne(String path, MethodType method) {
        for (Routing<T> l : table) {
            if (l.matches(method, path)) {
                return l.target();
            }
        }

        return null;
    }

    /**
     * 区配一个目标并给出状态
     *
     * @param path   路径
     * @param method 方法
     * @return 一个区配的目标
     */
    @Override
    public Result<T> matchOneAndStatus(String path, MethodType method) {
        int degrees = 0;
        for (Routing<T> l : table) {
            int tmp = l.degrees(method, path);
            if (tmp == 2) {
                return Result.succeed(l.target());
            } else {
                if (tmp > degrees) {
                    degrees = tmp;
                }
            }
        }

        if (degrees == 1) {
            return Result.failure(405);
        } else {
            return Result.failure(404);
        }
    }

    /**
     * 区配多个目标
     *
     * @param path   路径
     * @param method 方法
     * @return 一批区配的目标
     */
    public List<T> matchMore(String path, MethodType method) {
        return table.stream()
                .filter(l -> l.matches(method, path))
                .sorted(Comparator.comparingInt(l -> l.index()))
                .map(l -> l.target())
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        table.clear();
    }
}
