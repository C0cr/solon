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
package org.noear.solon.data.sqlink.core.exception;

import org.noear.solon.data.sqlink.base.DbType;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SQLinkLimitNotFoundOrderByException extends SQLinkException {
    public SQLinkLimitNotFoundOrderByException(DbType dbType) {
        super(dbType.name() + "数据库下进行的limit操作需要声明order by的字段，或者为表类指定一个主键");
    }
}
