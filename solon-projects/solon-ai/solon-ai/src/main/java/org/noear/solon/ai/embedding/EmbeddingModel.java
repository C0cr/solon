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

import org.noear.solon.lang.Preview;

import java.util.Arrays;
import java.util.List;

/**
 * 嵌入模型（相当于翻译器）
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface EmbeddingModel {

    default EmbeddingRequest input(String... input){
        return input(Arrays.asList(input));
    }

    EmbeddingRequest input(List<String> input);


    static EmbeddingModelBuilder of(EmbeddingConfig config) {
        return new EmbeddingModelBuilder(config);
    }

    static EmbeddingModelBuilder of(String apiUrl) {
        return new EmbeddingModelBuilder(apiUrl);
    }
}
