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
package org.noear.solon.flow.core;

import java.util.Map;

/**
 * 连接申明
 *
 * @author noear
 * @since 3.0
 */
public class LinkDecl {
    private final String toId;
    private String title;
    private Map<String, Object> meta;
    private String condition;
    private int priority;

    public LinkDecl(String toId) {
        this.toId = toId;
    }

    public LinkDecl title(String title) {
        this.title = title;
        return this;
    }

    public LinkDecl meta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public LinkDecl condition(String condition) {
        this.condition = condition;
        return this;
    }

    public LinkDecl priority(int priority) {
        this.priority = priority;
        return this;
    }

    public String toId() {
        return toId;
    }

    public String title() {
        return title;
    }

    public Map<String, Object> meta() {
        return meta;
    }

    public String condition() {
        return condition;
    }

    /**
     * 优先级（越大越高）
     * */
    public int priority() {
        return priority;
    }

    @Override
    public String toString() {
        return "LinkSpec{" +
                "toId='" + toId + '\'' +
                ", title='" + title + '\'' +
                ", meta=" + meta +
                ", condition='" + condition + '\'' +
                '}';
    }
}
