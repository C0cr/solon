/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Json 序列化器
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class SnackStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";

    private Options config;

    /**
     * 获取配置
     */
    public Options getConfig() {
        if (config == null) {
            config = Options.def();
        }

        return config;
    }

    /**
     * 设置配置
     */
    public void setConfig(Options config) {
        if (config != null) {
            this.config = config;
        }
    }

    /**
     * 获取内容类型
     */
    @Override
    public String contentType() {
        return "application/json";
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.contains(label);
        }
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "snack3-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        return ONode.loadObj(obj, getConfig()).toJson();
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        if (toType == null) {
            return ONode.loadStr(data, getConfig());
        } else {
            return ONode.loadStr(data, getConfig()).toObject(toType);
        }
    }

    /**
     * 序列化主体
     *
     * @param ctx  请求上下文
     * @param data 数据
     */
    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(contentType());

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    /**
     * 反序列化主体
     *
     * @param ctx 请求上下文
     */
    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return ONode.loadStr(data, getConfig());
        } else {
            return null;
        }
    }
}