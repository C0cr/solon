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
package org.noear.solon.ai.chat.message;

import org.noear.solon.ai.chat.ChatRole;

import java.util.List;

/**
 * 聊天消息
 *
 * @author noear
 * @since 3.1
 */
public interface ChatMessage {
    /**
     * 获取角色
     */
    ChatRole getRole();

    /**
     * 获取内容
     * */
    String getContent();


    /// //////////////

    static AssistantMessage ofAssistant(String content) {
        return new AssistantMessage(content, null, null, null);
    }

    /**
     * 构建系统消息
     */
    static ChatMessage ofSystem(String content) {
        return new SystemMessage(content);
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content) {
        return new UserMessage(content, null);
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content, List<String> imageUrls) {
        return new UserMessage(content, imageUrls);
    }

    /**
     * 构建工具消息
     */
    static ChatMessage ofTool(String content, String name, String id) {
        return new ToolMessage(content, name, id);
    }
}