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
import org.noear.solon.data.sqlink.base.annotation.OnUpdateDefaultValue;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.generate.DynamicGenerator;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 更新语句构造器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class UpdateSqlBuilder implements ISqlBuilder {
    private final SqLinkConfig config;
    private final ISqlJoinsExpression joins;
    private final ISqlSetsExpression sets;
    private final ISqlWhereExpression wheres;
    private final Class<?> target;
    private final SqlExpressionFactory factory;

    public UpdateSqlBuilder(SqLinkConfig config, Class<?> target) {
        this.config = config;
        this.target = target;
        factory = config.getSqlExpressionFactory();
        joins = factory.Joins();
        sets = factory.sets();
        wheres = factory.where();
    }

    /**
     * 添加关联表
     *
     * @param joinType 关联类型
     * @param table    关联表
     * @param on       关联条件
     */
    public void addJoin(JoinType joinType, ISqlTableExpression table, ISqlExpression on) {
        ISqlJoinExpression join = factory.join(
                joinType,
                table,
                on,
                1 + joins.getJoins().size()
        );
        joins.addJoin(join);
    }

    /**
     * 添加需要更新的列
     */
    public void addSet(ISqlSetsExpression set) {
        sets.addSet(set.getSets());
    }

    /**
     * 添加需要更新的列
     */
    public void addSet(ISqlSetExpression set) {
        sets.addSet(set);
    }

    /**
     * 添加条件
     */
    public void addWhere(ISqlExpression where) {
        wheres.addCondition(where);
    }

    /**
     * 是否有条件
     */
    public boolean hasWhere() {
        return !wheres.isEmpty();
    }

    @Override
    public String getSql() {
        return makeUpdate();
    }

    @Override
    public String getSqlAndValue(List<Object> values) {
        return makeUpdate(values);
    }

    public SqLinkConfig getConfig() {
        return config;
    }

    private String makeUpdate() {
        return makeUpdate(null);
    }

    private String makeUpdate(List<Object> values) {
        MetaData metaData = MetaDataCache.getMetaData(target);
        SqLinkDialect dbConfig = config.getDisambiguation();
        String sql = "UPDATE " + dbConfig.disambiguationTableName(metaData.getTableName()) + " AS t0";
        List<String> sb = new ArrayList<>();
        sb.add(sql);
        String joinsSqlAndValue = joins.getSqlAndValue(config, values);
        if (!joinsSqlAndValue.isEmpty()) {
            sb.add(joinsSqlAndValue);
        }
        StringBuilder setsSqlAndValue = new StringBuilder(sets.getSqlAndValue(config, values));
        setDefaultValues(values, metaData, setsSqlAndValue);
        sb.add(setsSqlAndValue.toString());
        String wheresSqlAndValue = wheres.getSqlAndValue(config, values);
        if (!wheresSqlAndValue.isEmpty()) {
            sb.add(wheresSqlAndValue);
        }
        return String.join(" ", sb);
    }

    private void setDefaultValues(List<Object> values, MetaData metaData, StringBuilder sb) {
        Set<FieldMetaData> fieldMetaDataSet = sets.getSets().stream().map(s -> s.getColumn().getPropertyMetaData()).collect(Collectors.toSet());
        List<FieldMetaData> notIgnorePropertys = metaData.getNotIgnorePropertys();
        for (FieldMetaData fieldMetaData : notIgnorePropertys) {
            OnUpdateDefaultValue onUpdate = fieldMetaData.getOnUpdateDefaultValues();
            if (!fieldMetaDataSet.contains(fieldMetaData) && onUpdate != null) {
                switch (onUpdate.strategy()) {
                    case DataBase:
                        // 交给数据库
                        break;
                    case Static: {
                        ISqlColumnExpression column = factory.column(fieldMetaData);
                        String columnSql = column.getSql(config);
                        SqlValue sqlValue = new SqlValue(onUpdate.value(), fieldMetaData.getTypeHandler(), true);
                        sb.append(",").append(columnSql).append(" = ?");
                        values.add(sqlValue);
                    }
                    break;
                    case Dynamic: {
                        ISqlColumnExpression column = factory.column(fieldMetaData);
                        String columnSql = column.getSql(config);
                        DynamicGenerator generator = DynamicGenerator.get(onUpdate.dynamic());
                        SqlValue sqlValue = new SqlValue(generator.generate(config, fieldMetaData), fieldMetaData.getTypeHandler());
                        sb.append(",").append(columnSql).append(" = ?");
                        values.add(sqlValue);
                    }
                    break;
                }
            }
        }
    }
}
