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
package org.noear.nami.coder.fury;

import org.noear.nami.Context;
import org.noear.nami.Decoder;
import org.noear.nami.Result;
import org.noear.nami.common.ContentTypes;

import java.lang.reflect.Type;

/**
 * Fury 解码器
 *
 * @author noear
 * @since 2.5
 * */
public class FuryDecoder implements Decoder {
    public static final FuryDecoder instance = new FuryDecoder();

    @Override
    public String enctype() {
        return ContentTypes.FURY_VALUE;
    }


    @Override
    public <T> T decode(Result rst, Type type) {
        if (rst.body().length == 0) {
            return null;
        }

        return (T) FuryUtil.fury.deserialize(rst.body());
    }

    @Override
    public void pretreatment(Context ctx) {
        ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_FURY);
        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.FURY_VALUE);
    }
}
