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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlQueryableExpression extends SqlTableExpression implements ISqlQueryableExpression {
    protected final ISqlSelectExpression select;
    protected final ISqlFromExpression from;
    protected final ISqlJoinsExpression joins;
    protected final ISqlWhereExpression where;
    protected final ISqlGroupByExpression groupBy;
    protected final ISqlHavingExpression having;
    protected final ISqlOrderByExpression orderBy;
    protected final ISqlLimitExpression limit;

    public SqlQueryableExpression(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit) {
        this.select = select;
        this.from = from;
        this.joins = joins;
        this.where = where;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values) {
        List<String> strings = new ArrayList<>();
        strings.add(getSelect().getSqlAndValue(config, values));
        String fromSqlAndValue = getFrom().getSqlAndValue(config, values);
        if (!fromSqlAndValue.isEmpty()) strings.add(fromSqlAndValue);
        String joinsSqlAndValue = getJoins().getSqlAndValue(config, values);
        if (!joinsSqlAndValue.isEmpty()) strings.add(joinsSqlAndValue);
        String whereSqlAndValue = getWhere().getSqlAndValue(config, values);
        if (!whereSqlAndValue.isEmpty()) strings.add(whereSqlAndValue);
        String groupBySqlAndValue = getGroupBy().getSqlAndValue(config, values);
        if (!groupBySqlAndValue.isEmpty()) strings.add(groupBySqlAndValue);
        String havingSqlAndValue = getHaving().getSqlAndValue(config, values);
        if (!havingSqlAndValue.isEmpty()) strings.add(havingSqlAndValue);
        String orderBySqlAndValue = getOrderBy().getSqlAndValue(config, values);
        if (!orderBySqlAndValue.isEmpty()) strings.add(orderBySqlAndValue);
        if (!getFrom().isEmptyTable()) {
            String limitSqlAndValue = getLimit().getSqlAndValue(config, values);
            if (!limitSqlAndValue.isEmpty()) strings.add(limitSqlAndValue);
        }
        return String.join(" ", strings);
    }


    @Override
    public Class<?> getTableClass() {
        return select.getTarget();
    }

    public void addWhere(ISqlExpression cond) {
        where.addCondition(cond);
    }

    public void addJoin(ISqlJoinExpression join) {
        joins.addJoin(join);
    }

    public void setGroup(ISqlGroupByExpression group) {
        groupBy.setColumns(group.getColumns());
    }

    public void addHaving(ISqlExpression cond) {
        having.addCond(cond);
    }

    public void addOrder(ISqlOrderExpression order) {
        orderBy.addOrder(order);
    }

    public void setSelect(ISqlSelectExpression newSelect) {
        select.setColumns(newSelect.getColumns());
        select.setTarget(newSelect.getTarget());
        select.setSingle(newSelect.isSingle());
        select.setDistinct(newSelect.isDistinct());
    }

    @Override
    public void addSelectColumn(ISqlExpression expression) {
        select.addColumn(expression);
    }

    public void setLimit(long offset, long rows) {
        limit.setOffset(offset);
        limit.setRows(rows);
    }

    public void setDistinct(boolean distinct) {
        select.setDistinct(distinct);
    }

    public ISqlFromExpression getFrom() {
        return from;
    }

    public int getOrderedCount() {
        return 1 + joins.getJoins().size();
    }

    public ISqlWhereExpression getWhere() {
        return where;
    }

    public ISqlGroupByExpression getGroupBy() {
        return groupBy;
    }

    public ISqlJoinsExpression getJoins() {
        return joins;
    }

    public ISqlSelectExpression getSelect() {
        return select;
    }

    public ISqlOrderByExpression getOrderBy() {
        return orderBy;
    }

    public ISqlLimitExpression getLimit() {
        return limit;
    }

    @Override
    public ISqlHavingExpression getHaving() {
        return having;
    }

    public List<Class<?>> getOrderedClass() {
        Class<?> tableClass = getTableClass();
        List<Class<?>> collect = joins.getJoins().stream().map(j -> j.getJoinTable().getTableClass()).collect(Collectors.toList());
        collect.add(0, tableClass);
        return collect;
    }
}
