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
package org.noear.solon.data.sqlink.core.expression.oracle;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;
import org.noear.solon.data.sqlink.base.expression.impl.SqlFromExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * Oracle From 表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public class OracleFromExpression extends SqlFromExpression {
    public OracleFromExpression(ISqlTableExpression sqlTableExpression, int index) {
        super(sqlTableExpression, index);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        // oracle 不支持 无 from 查询
        // 所以我们要加上DUAL表
        if (isEmptyTable()) return "FROM " + config.getDisambiguation().disambiguationTableName("DUAL");
        String sql;
        if (sqlTableExpression instanceof ISqlRealTableExpression) {
            sql = sqlTableExpression.getSqlAndValue(config, values);
        }
        else {
            sql = "(" + sqlTableExpression.getSqlAndValue(config, values) + ")";
        }
        return "FROM " + sql + " t" + index;
    }
}
