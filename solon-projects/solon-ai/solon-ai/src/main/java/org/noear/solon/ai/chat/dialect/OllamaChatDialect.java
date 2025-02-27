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
import org.noear.solon.Utils;
import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.chat.ChatException;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.ai.chat.message.UserMessage;
import org.noear.solon.ai.image.Image;
import org.noear.solon.core.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Ollama 聊天模型方言
 *
 * @author noear
 * @since 3.1
 */
public class OllamaChatDialect extends AbstractChatDialect {
    private static OllamaChatDialect instance = new OllamaChatDialect();

    public static OllamaChatDialect getInstance() {
        return instance;
    }

    /**
     * 匹配检测
     *
     * @param config 聊天配置
     */
    @Override
    public boolean matched(ChatConfig config) {
        return "ollama".equals(config.getProvider());
    }

    @Override
    protected void buildChatMessageNodeDo(ONode oNode, UserMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());
        if (Utils.isEmpty(msg.getImages())) {
            oNode.set("content", msg.getContent());
        } else {
            oNode.set("content", msg.getContent());
            oNode.set("images", msg.getImages());
        }
    }

    protected void buildChatMessageImageNodeDo(ONode oNode, Image img) {
        oNode.set("type", "image_url");

        if (Utils.isNotEmpty(img.getUrl())) {
            //url
            oNode.getOrNew("image_url").set("url", img.getUrl());
        } else {
            //base64
            oNode.getOrNew("image_url").set("url", img.getB64Json());
        }
    }

    @Override
    public boolean parseResponseJson(ChatConfig config, ChatResponseDefault resp, String json) {
        //解析
        ONode oResp = ONode.load(json);

        if (oResp.isObject() == false) {
            return false;
        }

        if (oResp.contains("error")) {
            resp.setError(new ChatException(oResp.get("error").getString()));
        } else {
            resp.setModel(oResp.get("model").getString());
            resp.setFinished(oResp.get("done").getBoolean());
            String done_reason = oResp.get("done_reason").getString();

            String createdStr = oResp.get("created_at").getString();
            if (createdStr != null) {
                createdStr = createdStr.substring(0, createdStr.indexOf(".") + 4);
            }
            Date created = DateUtil.parseTry(createdStr);
            AssistantMessage message1 = parseAssistantMessage(resp, oResp.get("message"));
            resp.addChoice(new ChatChoice(0, created, done_reason, message1));

            if (resp.isFinished()) {
                long promptTokens = oResp.get("prompt_eval_count").getLong();
                long completionTokens = oResp.get("eval_count").getLong();
                long totalTokens = promptTokens + completionTokens;

                resp.setUsage(new AiUsage(promptTokens, completionTokens, totalTokens));
            }
        }

        return true;
    }
}