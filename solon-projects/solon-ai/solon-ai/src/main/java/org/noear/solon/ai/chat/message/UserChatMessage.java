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

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatRole;

import java.util.List;

/**
 * 聊天用户消息
 *
 * @author noear
 * @since 3.1
 */
public class UserChatMessage implements ChatMessage {
    private final String content;
    private final List<String> imageUrls;

    public UserChatMessage(String content, List<String> imageUrls) {
        this.content = content;
        this.imageUrls = imageUrls;
    }

    @Override
    public ChatRole getRole() {
        return ChatRole.USER;
    }

    @Override
    public String getContent() {
        return content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    @Override
    public String toString() {
        if (Utils.isEmpty(imageUrls)) {
            return "{" +
                    "role='" + getRole() + '\'' +
                    ",content='" + content + '\'' +
                    '}';
        } else {
            return "{" +
                    "role='" + getRole() + '\'' +
                    ",content='" + content + '\'' +
                    ",image_urls='" + imageUrls + '\'' +
                    '}';
        }
    }
}