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
package org.noear.solon.serialization.jackson.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Type;

/**
 * @author noear
 * @since 1.2
 */
public class TypeReferenceImpl<T> extends TypeReference<T> {
    protected final Type _type2;

    public TypeReferenceImpl(ParamWrap p) {
        if (p.isGenericType()) {
            this._type2 = p.getGenericType();
        } else {
            this._type2 = p.getType();
        }
    }

    @Override
    public Type getType() {
        return _type2;
    }
}
