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
package org.noear.solon.ai.chat;

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天响应实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatResponseDefault implements ChatResponse {
    protected final List<ChatChoice> choices = new ArrayList<>();
    protected ChatException error;
    protected ChatUsage usage;
    protected String model;
    protected boolean finished;

    /**
     * 获取模型
     */
    @Override
    public String getModel() {
        return model;
    }

    /**
     * 获取错误
     */
    @Override
    public ChatException getError() {
        return error;
    }

    /**
     * 获取所有选择
     */
    @Override
    public List<ChatChoice> getChoices() {
        return choices;
    }

    /**
     * 是否有消息
     */
    @Override
    public boolean hasChoices() {
        return Utils.isNotEmpty(choices);
    }

    /**
     * 获取消息
     */
    @Override
    public AssistantMessage getMessage() {
        if (hasChoices()) {
            return choices.get(0).getMessage();
        } else {
            return null;
        }
    }

    /**
     * 获取使用情况（完成时，才会有使用情况）
     */
    @Override
    public @Nullable ChatUsage getUsage() {
        return usage;
    }

    /**
     * 是否完成
     */
    @Override
    public boolean isFinished() {
        return finished;
    }
}