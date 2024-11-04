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
package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class QuerySqlBuilder implements ISqlBuilder {
    private final IConfig config;
    private final ISqlQueryableExpression queryable;
    private final List<IncludeSet> includeSets = new ArrayList<>();
    private boolean isChanged;

//    public QuerySqlBuilder(IConfig config, Class<?> target, int offset)
//    {
//        this(config, config.getSqlExpressionFactory().table(target), offset);
//    }
//
//    public QuerySqlBuilder(IConfig config, Class<?> target)
//    {
//        this(config, config.getSqlExpressionFactory().table(target), 0);
//    }
//
//    public QuerySqlBuilder(IConfig config, ISqlTableExpression target)
//    {
//        this(config, target, 0);
//    }
//
//    public QuerySqlBuilder(IConfig config, ISqlTableExpression target, int offset)
//    {
//        this.config = config;
//        SqlExpressionFactory factory = config.getSqlExpressionFactory();
//        queryable = factory.queryable(factory.from(target, offset));
//    }

    public QuerySqlBuilder(IConfig config, ISqlQueryableExpression queryable) {
        this.config = config;
        this.queryable = queryable;
    }

    public void addWhere(ISqlExpression cond) {
        queryable.addWhere(cond);
        change();
    }

    public void addOrWhere(ISqlExpression cond) {
        if (queryable.getWhere().isEmpty()) {
            addWhere(cond);
        }
        else {
            SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
            addWhere(factory.unary(SqlOperator.OR, cond));
        }
    }

    public void addJoin(JoinType joinType, ISqlTableExpression table, ISqlExpression conditions) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlJoinExpression join = factory.join(joinType, table, conditions, queryable.getOrderedCount());
        queryable.addJoin(join);
        change();
    }

    public void setGroup(ISqlGroupByExpression group) {
        queryable.setGroup(group);
        change();
    }

    public void addHaving(ISqlExpression cond) {
        queryable.addHaving(cond);
        change();
    }

    public void addOrder(ISqlOrderExpression order) {
        queryable.addOrder(order);
        change();
    }

    public void setSelect(ISqlSelectExpression select) {
        queryable.setSelect(select);
        change();
    }

    public void setSelect(Class<?> c) {
        List<Class<?>> orderedClass = getOrderedClass();
        SqlExpressionFactory factory = getConfig().getSqlExpressionFactory();
        MetaData metaData = MetaDataCache.getMetaData(c);
        List<ISqlExpression> expressions = new ArrayList<>();
        if (orderedClass.contains(c)) {
            int index = orderedClass.indexOf(c);
            for (PropertyMetaData notIgnoreProperty : metaData.getNotIgnorePropertys()) {
                expressions.add(factory.column(notIgnoreProperty, index));
            }
        }
        else {
            for (PropertyMetaData sel : metaData.getNotIgnorePropertys()) {
                int index = 0;
                GOTO:
                for (MetaData data : MetaDataCache.getMetaData(getOrderedClass())) {
                    for (PropertyMetaData noi : data.getNotIgnorePropertys()) {
                        if (noi.getColumn().equals(sel.getColumn()) && noi.getType().equals(sel.getType())) {
                            expressions.add(factory.column(sel, index));
                            break GOTO;
                        }
                    }
                    index++;
                }
            }
        }
        queryable.setSelect(factory.select(expressions, c));
        change();
    }

    public void setLimit(long offset, long rows) {
        queryable.setLimit(offset, rows);
        change();
    }

    public void setDistinct(boolean distinct) {
        queryable.setDistinct(distinct);
        change();
    }

    private void change() {
        isChanged = true;
    }

    @Override
    public IConfig getConfig() {
        return config;
    }

    @Override
    public String getSql() {
        if (isChanged) {
            return queryable.getSql(config);
        }
        else {
            ISqlTableExpression sqlTableExpression = queryable.getFrom().getSqlTableExpression();
            if (sqlTableExpression instanceof ISqlRealTableExpression) {
                return queryable.getSql(config);
            }
            return sqlTableExpression.getSql(config);
        }
    }

    @Override
    public String getSqlAndValue(List<Object> values) {
        if (isChanged) {
            return queryable.getSqlAndValue(config, values);
        }
        else {
            ISqlTableExpression sqlTableExpression = queryable.getFrom().getSqlTableExpression();
            if (sqlTableExpression instanceof ISqlRealTableExpression) {
                return queryable.getSqlAndValue(config, values);
            }
            return sqlTableExpression.getSqlAndValue(config, values);
        }
    }

//    public String getSqlAndValueAndFirst(List<Object> values)
//    {
//        if (isChanged)
//        {
//            return queryable.getSqlAndValueAndFirst(config, values);
//        }
//        else
//        {
//            SqlTableExpression sqlTableExpression = queryable.getFrom().getSqlTableExpression();
//            if (sqlTableExpression instanceof SqlRealTableExpression)
//            {
//                return queryable.getSqlAndValueAndFirst(config, values);
//            }
//            else
//            {
//                SqlQueryableExpression tableExpression = (SqlQueryableExpression) sqlTableExpression;
//                return tableExpression.getSqlAndValueAndFirst(config, values);
//            }
//        }
//    }

    public List<Class<?>> getOrderedClass() {
        return queryable.getOrderedClass();
    }

    public List<PropertyMetaData> getMappingData() {
        if (isChanged) {
            return queryable.getMappingData(config);
        }
        else {
            ISqlTableExpression sqlTableExpression = queryable.getFrom().getSqlTableExpression();
            if (sqlTableExpression instanceof ISqlRealTableExpression) {
                return queryable.getMappingData(config);
            }
            else {
                ISqlQueryableExpression tableExpression = (ISqlQueryableExpression) sqlTableExpression;
                return tableExpression.getMappingData(config);
            }
        }
    }

    public boolean isSingle() {
        return queryable.getSelect().isSingle();
    }

    public Class<?> getTargetClass() {
        return queryable.getSelect().getTarget();
    }

    public ISqlQueryableExpression getQueryable() {
        return queryable;
    }

    public List<IncludeSet> getIncludeSets() {
        return includeSets;
    }

    public IncludeSet getLastIncludeSet() {
        return includeSets.get(includeSets.size() - 1);
    }
}
