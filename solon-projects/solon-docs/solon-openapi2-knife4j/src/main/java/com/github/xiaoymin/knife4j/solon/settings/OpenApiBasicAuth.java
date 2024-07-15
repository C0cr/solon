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
package com.github.xiaoymin.knife4j.solon.settings;

import org.noear.solon.docs.BasicAuth;

/**
 * @author noear
 * @since 2.3
 */
public class OpenApiBasicAuth implements BasicAuth {
    boolean enable = true;
    String username;
    String password;

    public boolean isEnable() {
        return enable;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
