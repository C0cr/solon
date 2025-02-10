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
package org.noear.solon.ai.chat.dialect;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiException;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.impl.ChatResponseDefault;
import org.noear.solon.core.util.DateUtil;

import java.util.Date;

/**
 * Ollama 方言
 *
 * @author noear
 * @since 3.1
 */
public class OllamaDialect extends AbstractDialect {
    private static OllamaDialect instance = new OllamaDialect();

    public static OllamaDialect getInstance() {
        return instance;
    }

    @Override
    public String provider() {
        return "ollama";
    }

    @Override
    public boolean resolveResponseJson(ChatConfig config, ChatResponseDefault resp, String json) {
        //解析
        ONode oResp = ONode.load(json);

        if (oResp.isObject() == false) {
            return false;
        }

        if (oResp.contains("error")) {
            resp.exception = new AiException(oResp.get("error").getString());
        } else {
            resp.model = oResp.get("model").getString();
            resp.finished = oResp.get("done").getBoolean();
            String done_reason = oResp.get("done_reason").getString();

            String createdStr = oResp.get("created_at").getString();
            if (createdStr != null) {
                createdStr = createdStr.substring(0, createdStr.indexOf(".") + 4);
            }
            Date created = DateUtil.parseTry(createdStr);
            resp.choices.clear();
            resp.choices.add(new ChatChoice(0, created, done_reason, ChatMessage.ofAssistant(config, oResp.get("message"))));

//            if (resp.finished) {
//                resp.total_duration = oResp.get("total_duration").getLong();
//                resp.load_duration = oResp.get("load_duration").getLong();
//                resp.prompt_eval_count = oResp.get("prompt_eval_count").getLong();
//                resp.prompt_eval_duration = oResp.get("prompt_eval_duration").getLong();
//                resp.eval_count = oResp.get("eval_count").getLong();
//                resp.eval_duration = oResp.get("eval_duration").getLong();
//            }
        }

        return true;
    }
}