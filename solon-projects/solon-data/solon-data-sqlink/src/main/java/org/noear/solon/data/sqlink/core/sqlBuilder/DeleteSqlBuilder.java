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

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 删除语句生成器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class DeleteSqlBuilder implements ISqlBuilder {
    private final SqLinkConfig config;
    private final ISqlJoinsExpression joins;
    private final ISqlWhereExpression wheres;
    private final Class<?> target;
    private final Set<Integer> excludes = new HashSet<>();
    private final SqlExpressionFactory factory;
    private final List<Class<?>> orderedClasses = new ArrayList<>();

    public DeleteSqlBuilder(SqLinkConfig config, Class<?> target) {
        this.config = config;
        this.target = target;
        factory = config.getSqlExpressionFactory();
        joins = factory.Joins();
        wheres = factory.where();
        orderedClasses.add(target);
    }

    /**
     * 添加关联表
     * @param joinType 关联类型
     * @param table 关联表
     * @param on 关联条件
     */
    public void addJoin(JoinType joinType, ISqlTableExpression table, ISqlExpression on) {
        ISqlJoinExpression join = factory.join(
                joinType,
                table,
                on,
                1 + joins.getJoins().size()
        );
        joins.addJoin(join);
        orderedClasses.add(table.getTableClass());
    }

    /**
     * 添加指定删除的表
     */
    public void addExclude(Class<?> c) {
        excludes.add(orderedClasses.indexOf(c));
    }

    /**
     * 添加删除的where条件
     */
    public void addWhere(ISqlExpression where) {
        wheres.addCondition(where);
    }

    @Override
    public SqLinkConfig getConfig() {
        return config;
    }

    /**
     * 是否有where条件
     */
    public boolean hasWhere() {
        return !wheres.isEmpty();
    }

    @Override
    public String getSql() {
        List<String> strings = new ArrayList<>(3);
        String sql = makeDelete();
        strings.add(sql);
        String joinsSql = joins.getSql(config);
        if (!joinsSql.isEmpty()) {
            strings.add(joinsSql);
        }
        String wheresSql = wheres.getSql(config);
        if (!wheresSql.isEmpty()) {
            strings.add(wheresSql);
        }
        return String.join(" ", strings);
    }

    @Override
    public String getSqlAndValue(List<Object> values) {
        List<String> strings = new ArrayList<>(3);
        String sql = makeDelete();
        strings.add(sql);
        String joinsSql = joins.getSqlAndValue(config, values);
        if (!joinsSql.isEmpty()) {
            strings.add(joinsSql);
        }
        String wheresSql = wheres.getSqlAndValue(config, values);
        if (!wheresSql.isEmpty()) {
            strings.add(wheresSql);
        }
        return String.join(" ", strings);
    }

    private String makeDelete() {
        StringBuilder builder = new StringBuilder("DELETE");
        if (!excludes.isEmpty()) {
            builder.append(" ");
            List<String> strings = new ArrayList<>(excludes.size());
            for (int index : excludes) {
                if (index != -1) {
                    strings.add("t" + index);
                }
            }
            builder.append(String.join(",", strings));
        }
        SqLinkDialect dbConfig = config.getDisambiguation();
        MetaData metaData = MetaDataCache.getMetaData(target);
        return builder.append(" FROM ").append(dbConfig.disambiguationTableName(metaData.getTableName())).append(" AS t0").toString();
    }
}
