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
package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlFromExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlFromExpression implements ISqlFromExpression
{
    protected final ISqlTableExpression sqlTableExpression;
    protected final int index;

    public SqlFromExpression(ISqlTableExpression sqlTableExpression, int index)
    {
        this.sqlTableExpression = sqlTableExpression;
        this.index = index;
    }

    @Override
    public ISqlTableExpression getSqlTableExpression()
    {
        return sqlTableExpression;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (isEmptyTable()) return "";
        String sql;
        if (getSqlTableExpression() instanceof ISqlRealTableExpression)
        {
            sql = getSqlTableExpression().getSqlAndValue(config, values);
        }
        else
        {
            sql = "(" + getSqlTableExpression().getSqlAndValue(config, values) + ")";
        }
        String t = "t" + getIndex();
        return "FROM " + sql + " AS " + config.getDisambiguation().disambiguation(t);
    }
}
