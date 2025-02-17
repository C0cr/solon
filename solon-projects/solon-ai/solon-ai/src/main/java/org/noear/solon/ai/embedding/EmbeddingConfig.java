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
package org.noear.solon.ai.embedding;

import org.noear.solon.ai.embedding.dialect.EmbeddingDialect;
import org.noear.solon.lang.Preview;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 嵌入配置
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class EmbeddingConfig {
    protected String apiUrl;
    protected String apiKey;
    protected String provider;
    protected String model;
    protected final Map<String, String> headers = new LinkedHashMap<>();
    protected Duration timeout = Duration.ofSeconds(30);

    protected transient EmbeddingDialect dialect;

    public String apiKey() {
        return apiKey;
    }

    public String apiUrl() {
        return apiUrl;
    }

    public String provider() {
        return provider;
    }

    public String model() {
        return model;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Duration timeout() {
        return timeout;
    }

    public EmbeddingDialect dialect() {
        return dialect;
    }
}
