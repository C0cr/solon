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
import org.noear.solon.data.sqlink.base.expression.ISqlCollectedValueExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SqlCollectedValueExpression implements ISqlCollectedValueExpression
{
    private final Collection<Object> collection;
    private String delimiter = ",";

    public SqlCollectedValueExpression(Collection<Object> collection)
    {
        this.collection = collection;
    }

    @Override
    public Collection<Object> getCollection()
    {
        return collection;
    }

    @Override
    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }

    @Override
    public String getDelimiter()
    {
        return delimiter;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        List<String> strings = new ArrayList<>(getCollection().size());
        for (Object obj : getCollection())
        {
            strings.add("?");
            if (values != null) values.add(obj);
        }
        return String.join(getDelimiter(), strings);
    }
}
