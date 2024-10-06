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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.MessageBuilder;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 消息编解码器实现
 *
 * @author limliu
 * @since 2.7
 */
public class MessageCodecImpl implements MessageCodec {
    //Command 结束符
    private String commandEnd = "\n";
    //Headers 结束符
    private String headersEnd = "\n\n";
    //Header 之间的分隔符
    private String headerDelimiter = "\n";
    //Header 键值 间的分隔符
    private String headerKvDelimiter = ":";
    //Body 结束符
    private String bodyEnd = "\u0000";

    //锁
    private ReentrantLock LOCK = new ReentrantLock();


    @Override
    public String encode(Message input) {
        StringBuilder buf = new StringBuilder();

        //command
        buf.append(input.getCommand());
        buf.append(commandEnd);

        //headers
        if (input.getHeaderAll().size() > 0) {
            for (KeyValue<String> kv : input.getHeaderAll()) {
                buf.append(kv.getKey())
                        .append(headerKvDelimiter)
                        .append(kv.getValue());

                buf.append(headerDelimiter);
            }

            buf.setLength(buf.length() - 1);
        }

        buf.append(headersEnd);

        //payload
        if (input.getPayload() != null) {
            buf.append(input.getPayload());
        }
        buf.append(bodyEnd);

        return buf.toString();
    }

    /**
     * 待解析的 Stomp 报文容器
     */
    private final StringBuilder PENDING = new StringBuilder();

    @Override
    public void decode(String input, Consumer<Message> out) {
        if (Utils.isEmpty(input)) {
            return;
        }

        LOCK.lock();
        try {
            // 装入待解析容器
            PENDING.append(input);
            // 开始解析
            decode(out, 0);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * @param out   输出
     * @param start 开始解析的问题
     */
    protected void decode(Consumer<Message> out, int start) {
        cleanPendingStartData(start);

        // Body 结尾符下标
        int bEndIdx = PENDING.indexOf(bodyEnd);
        if (bEndIdx < 0) {
            // 数据包尚未接收完毕，直接返回
            return;
        }

        // Command 结尾符下标
        int cEndIdx = PENDING.indexOf(commandEnd);

        // Headers 结尾符下标
        int hEndIdx = PENDING.indexOf(headersEnd);
        if (cEndIdx <= 0 || hEndIdx <= cEndIdx || bEndIdx <= hEndIdx) {
            // 非法数据包，跳过一个字符重新解析
            this.decode(out, 1);
            return;
        }

        // 解析 Command
        String command = PENDING.substring(0, cEndIdx).trim();
        if (!isCommand(command)) {
            // 非法数据包，跳过一个字符重新解析
            this.decode(out, 1);
            return;
        }

        MessageBuilder builder = Message.newBuilder();
        builder.command(command);

        // 解析 Headers
        this.decodeHeaders(builder, cEndIdx, hEndIdx);

        // 解析 Body
        String payload = PENDING.substring(hEndIdx + headersEnd.length(), bEndIdx);
        builder.payload(payload);

        // 输出解析结果
        out.accept(builder.build());

        // 重新解析 bodyEnd 之后的数据
        this.decode(out, bEndIdx + bodyEnd.length());
    }

    protected void cleanPendingStartData(int start) {
        if (start > 0) {
            // 清空 start 之前的数据
            PENDING.delete(0, start);
        }

        // Command 开始符下标
        int index = 0;
        for (int i = 0; i < PENDING.length(); i++) {
            char c = PENDING.charAt(i);
            if (this.isCommandChar(c)) {
                index = i;
                break;
            }
        }

        if (index > 0) {
            // 清空 Command 之前的数据
            PENDING.delete(0, index);
        }
    }

    /**
     * 解析 Headers
     *
     * @param cEndIdx Command 结尾符下标
     * @param hEndIdx Headers 结尾符下标
     * @return Headers
     */
    protected void decodeHeaders(MessageBuilder builder,int cEndIdx, int hEndIdx) {
        String[] strHeaders = PENDING.substring(cEndIdx + commandEnd.length(), hEndIdx).split(headerDelimiter);

        for (String header : strHeaders) {
            if (header == null) {
                continue;
            }

            int start = header.indexOf(headerKvDelimiter);
            if (start < 1) {
                continue;
            }

            String name = header.substring(0, start);
            String value = header.substring(start + headerKvDelimiter.length(), header.length());

            builder.header(name, value);
        }
    }

    protected boolean isCommand(String command) {
        return !command.isEmpty() && command.matches("[A-Z]+");
    }

    protected boolean isCommandChar(char c) {
        return c >= 'A' && c <= 'Z';
    }
}