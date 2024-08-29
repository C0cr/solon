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
package org.noear.solon.docs;

/**
 * 文档类型
 *
 * @author noear
 * @since 2.4
 */
public enum DocType {
    SWAGGER_2("2.0"),
    SWAGGER_3("3.0");

    String version;

    DocType(String version) {
        this.version = version;
    }

    /**
     * 获取版本号
     * */
    public String getVersion() {
        return version;
    }
}
