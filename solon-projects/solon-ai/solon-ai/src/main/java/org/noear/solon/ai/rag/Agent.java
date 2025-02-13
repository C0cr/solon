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
package org.noear.solon.ai.rag;

import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatRequest;
import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.rag.loader.DocumentLoader;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.lang.Preview;

/**
 * 智能体
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface Agent {
    static Agent of() {
        return null;
    }

    Agent model(ChatModel chatModel);
    Agent repository(Repository repository);
    Agent flow(FlowEngine flowEngine);
    Agent build();


    ChatRequest prompt(String prompt);

    void load(DocumentLoader loader);

    void load(ChatFunction function);
}
