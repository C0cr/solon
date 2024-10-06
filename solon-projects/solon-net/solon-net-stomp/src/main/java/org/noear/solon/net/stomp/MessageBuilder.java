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
package org.noear.solon.net.stomp;

import org.noear.solon.Utils;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.net.stomp.common.Headers;
import org.noear.solon.net.stomp.common.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author noear
 * @since
 */
public class MessageBuilder {
    private String command;
    private List<KeyValue<String>> headers = new ArrayList<>();
    private String payload;

    public MessageBuilder command(String command) {
        this.command = command;
        return this;
    }

    public MessageBuilder payload(String payload) {
        this.payload = payload;
        return this;
    }

    public MessageBuilder headers(KeyValue<String>... headers) {
        for (KeyValue<String> header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    public MessageBuilder headers(Iterable<KeyValue<String>> headers) {
        for (KeyValue<String> header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    public MessageBuilder header(String key, String val) {
        if (key != null && val != null) {
            headers.add(new KeyValue<>(key, val));
        }

        return this;
    }

    public MessageBuilder contentType(String contentType) {
        if (Utils.isNotEmpty(contentType)) {
            return header(Headers.CONTENT_TYPE, contentType);
        }

        return this;
    }

    public MessageBuilder destination(String destination) {
        if (Utils.isNotEmpty(destination)) {
            return header(Headers.DESTINATION, destination);
        }

        return this;
    }

    public Message build() {
        return new MessageImpl(command, payload, headers);
    }


    static class MessageImpl implements Message {
        private final String command;
        private final List<KeyValue<String>> headers;
        private final String payload;

        public MessageImpl(String command, String payload, List<KeyValue<String>> headers) {
            if (Utils.isEmpty(command)) {
                this.command = Commands.MESSAGE;
            } else {
                this.command = command;
            }

            this.payload = payload;
            this.headers = headers;
        }

        @Override
        public String getCommand() {
            return command;
        }

        @Override
        public String getPayload() {
            return payload;
        }

        @Override
        public String getHeader(String key) {
            if (headers == null) {
                return null;
            }

            Iterator<KeyValue<String>> iterator = headers.iterator();
            while (iterator.hasNext()) {
                KeyValue<String> h = iterator.next();
                if (key.equals(h.getKey())) {
                    return h.getValue();
                }
            }

            return null;
        }

        @Override
        public Collection<KeyValue<String>> getHeaderAll() {
            return headers;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "command='" + command + '\'' +
                    ", headers=" + headers +
                    ", payload='" + payload + '\'' +
                    '}';
        }
    }
}