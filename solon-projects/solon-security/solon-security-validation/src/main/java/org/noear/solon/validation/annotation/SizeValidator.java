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
package org.noear.solon.validation.annotation;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.util.Collection;

/**
 *
 * @author noear
 * @since 1.5
 * */
public class SizeValidator implements Validator<Size> {
    public static final SizeValidator instance = new SizeValidator();

    @Override
    public String message(Size anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(Size anno) {
        return anno.groups();
    }

    @Override
    public boolean isSupportValueType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @Override
    public Result validateOfValue(Size anno, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof Collection == false) {
            return Result.failure();
        }

        Collection val = (Collection) val0;

        if (verify(anno, val) == false) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Size anno, String name, StringBuilder tmp) {
        return Result.failure();
    }

    private boolean verify(Size anno, Collection val) {
        //如果为空，算通过（交由@NotNull之类，进一步控制）
        if (val == null) {
            return true;
        }

        if (anno.min() > 0 && val.size() < anno.min()) {
            return false;
        }

        if (anno.max() > 0 && val.size() > anno.max()) {
            return false;
        }

        return true;
    }
}
