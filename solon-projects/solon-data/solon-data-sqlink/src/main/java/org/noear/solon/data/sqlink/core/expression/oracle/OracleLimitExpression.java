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
package org.noear.solon.data.sqlink.core.expression.oracle;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.impl.SqlLimitExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * oracle limit 表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public class OracleLimitExpression extends SqlLimitExpression {
    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (onlyHasRows()) {
            return String.format("FETCH NEXT %d ROWS ONLY", rows);
        }
        else if (hasRowsAndOffset()) {
            return String.format("OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", offset, rows);
        }
        return "";
    }
}
