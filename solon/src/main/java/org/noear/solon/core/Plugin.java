/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.core;

/**
 * 通用插件接口（实现 Plugin 架构；通过Solon SPI进行申明）
 *
 * @author noear
 * @since 1.0
 * */
public interface Plugin {
    /**
     * 启动（保留，为兼容性过度）
     *
     * @param context 应用上下文
     */
    void start(AppContext context) throws Throwable;


    /**
     * 预停止
     */
    default void prestop() throws Throwable {
    }

    /**
     * 停止
     */
    default void stop() throws Throwable {
    }
}
