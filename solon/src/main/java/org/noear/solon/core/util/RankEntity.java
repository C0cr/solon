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
package org.noear.solon.core.util;

import java.util.Objects;

/**
 * 排序载体
 *
 * @author noear
 * @since 1.6
 */
public class RankEntity<T> implements Comparable<RankEntity<T>> {
    /**
     * 目标
     */
    public final T target;
    /**
     * 顺序
     */
    public final int index;
    /**
     * 优先级
     */
    public final int priority;

    public RankEntity(T t, int i) {
        this(t, i, 0);
    }

    public RankEntity(T t, int i, int p) {
        target = t;
        index = i;
        priority = p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RankEntity)) return false;
        RankEntity that = (RankEntity) o;
        return Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target);
    }

    /**
     * @since 2.9
     */
    @Override
    public int compareTo(RankEntity<T> o) {
        if (this.index == o.index) {
            if (this.priority == o.priority) {
                return 0;
            } else if (this.priority < o.priority) {
                return -1;
            } else {
                return 1;
            }
        } else if (this.index < o.index) {
            return -1;
        } else {
            return 1;
        }
    }
}
