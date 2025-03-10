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

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.util.StringUtils;

/**
 *
 * @author noear
 * @since 1.11
 * */
public class RangeValidator implements Validator<Range> {
    public static final RangeValidator instance = new RangeValidator();

    @Override
    public String message(Range anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(Range anno) {
        return anno.groups();
    }

    @Override
    public boolean isSupportValueType(Class<?> type) {
        return ClassUtil.isNumberType(type);
    }

    @Override
    public Result validateOfValue(Range anno, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof Number == false) {
            return Result.failure();
        }

        Number val = (Number) val0;

        if (verify(anno, val) == false) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Range anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);

        //如果为空，算通过（交由 @NotNull 或 @NotEmpty 或 @NotBlank 进一步控制）
        if (Utils.isEmpty(val)) {
            return Result.succeed();
        }

        if (StringUtils.isInteger(val) == false) {
            return Result.failure(name);
        }

        if (verify(anno, Long.parseLong(val)) == false) {
            return Result.failure(name);
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Range anno, Number val) {
        //如果为空，算通过（交由 @NotNull 进一步控制）
        if (val == null) {
            return true;
        }

        if (anno.min() > 0 && val.longValue() < anno.min()) {
            return false;
        }

        if (anno.max() > 0 && val.longValue() > anno.max()) {
            return false;
        }

        return true;
    }
}
