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
package demo.sqlink;

import demo.sqlink.model.User;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.core.sqlExt.SqlFunctions;

import java.util.List;

@Component
public class SelectDemoService {
    @Inject // or @Inject("main")
    SqLink sqLink;

    public String helloWorld(String value) {
        return sqLink.queryEmptyTable()
                // CONCAT_WS(' ','hello',{value})
                .endSelect(() -> SqlFunctions.join(" ", "hello", value))
                .first();
    }

    // 根据id查询用户
    public User findById(long id) {
        return sqLink.query(User.class)
                .where(user -> user.getId() == id)
                .first();
    }

    // 根据名称查询模糊匹配用户
    public List<User> findByName(String name) {
        return sqLink.query(User.class)
                // username LIKE '{name}%'
                .where(u -> u.getUsername().startsWith(name))
                .toList();
    }

    // 根据名称查询模糊匹配用户, 并且以匿名对象形式返回我们感兴趣的数据
    public List<? extends Result> findResultByName(String name) {
        return sqLink.query(User.class)
                // username LIKE '{name}%'
                .where(u -> u.getUsername().startsWith(name))
                .select(u -> new Result() {
                    long id = u.getId();
                    String email = u.getEmail();
                }).toList();
    }
}
