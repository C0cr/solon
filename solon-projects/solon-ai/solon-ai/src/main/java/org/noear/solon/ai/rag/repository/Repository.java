/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.ai.rag.repository;

import org.noear.solon.ai.rag.Document;
import org.noear.solon.lang.Preview;

import java.util.List;

/**
 * 文档仓库（向量存储与索引）
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface Repository {
    /**
     * 推入
     */
    void put(List<Document> documents);

    /**
     * 移除
     */
    void remove(String id);

    /**
     * 相似性搜索
     */
    List<Document> search(String message);
}