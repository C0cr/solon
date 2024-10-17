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
package org.noear.solon.net.stomp.listener;

import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.websocket.WebSocket;

/**
 * Stomp 服务端消息监听器
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public interface StompListener {

    /**
     * 连接打开时（可以鉴权；参数通过url和head方式指定）
     *
     * @param socket
     */
    void onOpen(WebSocket socket);

    /**
     * 收到消息帧
     *
     * @param socket
     * @param frame  帧
     */
    void onFrame(WebSocket socket, Frame frame);

    /**
     * 连接关闭时（被动监听；当断开时触发）
     */
    void onClose(WebSocket socket);

    /**
     * 出错时
     */
    void onError(WebSocket socket, Throwable error);
}
