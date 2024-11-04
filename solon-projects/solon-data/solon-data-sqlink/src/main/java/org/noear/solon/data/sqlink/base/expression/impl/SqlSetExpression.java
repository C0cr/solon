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
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlSetExpression implements ISqlSetExpression {
    private final ISqlColumnExpression column;
    private final ISqlExpression value;

    SqlSetExpression(ISqlColumnExpression column, ISqlExpression value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public ISqlColumnExpression getColumn() {
        return column;
    }

    @Override
    public ISqlExpression getValue() {
        return value;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values) {
        String set = getColumn().getSqlAndValue(config, values) + " = ";
        PropertyMetaData propertyMetaData = getColumn().getPropertyMetaData();
        if (propertyMetaData.isUseTypeHandler() && getValue() instanceof ISqlValueExpression) {
            if (getValue() instanceof ISqlSingleValueExpression) {
                ISqlSingleValueExpression sqlSingleValueExpression = (ISqlSingleValueExpression) getValue();
                values.add(new SqlValue(sqlSingleValueExpression.getValue(), propertyMetaData.getTypeHandler()));
                return set + "?";
            }
            else {
                ISqlCollectedValueExpression sqlCollectedValueExpression = (ISqlCollectedValueExpression) getValue();
                values.add(new SqlValue(sqlCollectedValueExpression.getCollection(), propertyMetaData.getTypeHandler()));
                return set + "?";
            }
        }
        else {
            return set + getValue().getSqlAndValue(config, values);
        }
    }
}
