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
package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

import java.lang.reflect.Type;


/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @since 1.5
 */
public abstract class FastjsonRenderFactoryBase implements JsonRenderFactory {
    protected final FastjsonStringSerializer serializer = new FastjsonStringSerializer();

    /**
     * 获取序列化器
     */
    public FastjsonStringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 序列化配置
     */
    public SerializeConfig config() {
        return serializer.getSerializeConfig();
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public void addEncoder(Type clz, ObjectSerializer encoder) {
        config().put(clz, encoder);

        if (clz == Long.class) {
            config().put(Long.TYPE, encoder);
        } else if (clz == Integer.class) {
            config().put(Integer.TYPE, encoder);
        }
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T, Object> converter) {
        addEncoder(clz, (ser, obj, fieldName, fieldType, features) -> {
            Object val = converter.convert((T) obj);

            SerializeWriter out = ser.getWriter();

            if (val == null) {
                out.writeNull();
            } else if (val instanceof String) {
                out.writeString((String) val);
            } else if (val instanceof Number) {
                if (val instanceof Integer || val instanceof Long) {
                    out.writeLong(((Number) val).longValue());
                } else {
                    out.writeDouble(((Number) val).doubleValue(), false);
                }
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}