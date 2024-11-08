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
package org.noear.solon.data.sqlink;

import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.api.crud.create.ObjectInsert;
import org.noear.solon.data.sqlink.api.crud.delete.LDelete;
import org.noear.solon.data.sqlink.api.crud.read.EmptyQuery;
import org.noear.solon.data.sqlink.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.api.crud.update.LUpdate;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.transaction.Transaction;

import java.util.Collection;

/**
 * 发起数据库操作接口
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface SqLink {

    /**
     * 手动开始事务
     *
     * @param isolationLevel 事务级别
     * @return 事务对象
     */
    Transaction beginTransaction(Integer isolationLevel);

    /**
     * 手动开始事务
     *
     * @return 事务对象
     */
    default Transaction beginTransaction() {
        return beginTransaction(null);
    }

    /**
     * 查询
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 查询过程对象
     */
    <T> LQuery<T> query(@Recode Class<T> c);

    /**
     * 进行不包含表的查询
     *
     * @return 查询过程对象
     */
    EmptyQuery queryEmptyTable();

    /**
     * 新增
     *
     * @param t   数据类对象
     * @param <T> 数据类类型
     * @return 新增过程对象
     */
    <T> ObjectInsert<T> insert(@Recode T t);

    /**
     * 集合新增
     *
     * @param ts  数据类对象集合
     * @param <T> 数据类类型
     * @return 新增过程对象
     */
    <T> ObjectInsert<T> insert(@Recode Collection<T> ts);

    /**
     * 更新
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 更新过程对象
     */
    <T> LUpdate<T> update(@Recode Class<T> c);

    /**
     * 删除
     *
     * @param c   数据类类对象
     * @param <T> 数据类类型
     * @return 删除过程对象
     */
    <T> LDelete<T> delete(@Recode Class<T> c);

    /**
     * 获取配置
     */
    SqLinkConfig getConfig();
}
