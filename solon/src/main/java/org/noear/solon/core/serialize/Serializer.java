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
package org.noear.solon.core.serialize;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 序列化器
 *
 * @author noear
 * @since 2.8
 */
public interface Serializer<T> {
    /**
     * 名字
     */
    String name();

    /**
     * 序列化
     */
    T serialize(Object fromObj) throws IOException;

    /**
     * 反序列化
     */
    Object deserialize(T data, Type toType) throws IOException;
}
