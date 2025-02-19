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

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.dialect.ChatDialect;
import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.chat.functioncall.ChatFunctionCall;
import org.noear.solon.ai.chat.functioncall.ChatFunctionParam;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.rx.SimpleSubscription;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 聊天请求实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatRequestDefault implements ChatRequest {
    private static final Logger log = LoggerFactory.getLogger(ChatRequestDefault.class);
    private static final ChatOptions OPTIONS_DEFAULT = new ChatOptions();

    private final ChatConfig config;
    private final ChatDialect dialect;
    private final List<ChatMessage> messages;

    private ChatOptions options;

    public ChatRequestDefault(ChatConfig config, ChatDialect dialect, List<ChatMessage> messages) {
        this.config = config;
        this.dialect = dialect;
        this.messages = messages;
        this.options = OPTIONS_DEFAULT;
    }

    /**
     * 选项设置
     */
    @Override
    public ChatRequest options(ChatOptions options) {
        if (options != null) {
            this.options = options;
        }

        return this;
    }

    /**
     * 选项配置
     */
    @Override
    public ChatRequest options(Consumer<ChatOptions> optionsBuilder) {
        this.options = ChatOptions.of();
        optionsBuilder.accept(options);
        return this;
    }

    /**
     * 调用
     */
    @Override
    public ChatResponse call() throws IOException {
        HttpUtils httpUtils = config.createHttpUtils();

        String reqJson = dialect.buildRequestJson(config, options, messages, false);

        if (log.isTraceEnabled()) {
            log.trace("ai-request: {}", reqJson);
        }

        String respJson = httpUtils.bodyOfJson(reqJson).post();

        if (log.isTraceEnabled()) {
            log.trace("ai-response: {}", respJson);
        }

        ChatResponseDefault resp = new ChatResponseDefault();
        dialect.parseResponseJson(config, resp, respJson);

        if (resp.getError() != null) {
            throw resp.getError();
        }

        if (resp.hasChoices()) {
            AssistantMessage choiceMessage = resp.getMessage();
            if (Utils.isNotEmpty(choiceMessage.getToolCalls())) {
                messages.add(choiceMessage);
                buildToolMessage(choiceMessage);

                return call();
            }
        }

        return resp;
    }

    /**
     * 流响应
     */
    @Override
    public Publisher<ChatResponse> stream() {
        HttpUtils httpUtils = config.createHttpUtils();

        String reqJson = dialect.buildRequestJson(config, options, messages, true);

        if (log.isTraceEnabled()) {
            log.trace("ai-request: {}", reqJson);
        }

        return subscriber -> {
            httpUtils.bodyOfJson(reqJson).execAsync("POST")
                    .whenComplete((resp, err) -> {
                        if (err == null) {
                            try {
                                parseResp(resp, subscriber);
                            } catch (IOException e) {
                                subscriber.onError(e);
                            }
                        } else {
                            subscriber.onError(err);
                        }
                    });
        };
    }

    private void parseResp(HttpResponse httpResp, Subscriber<? super ChatResponse> subscriber) throws IOException {
        ChatResponseDefault resp = new ChatResponseDefault();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResp.body()))) {
            subscriber.onSubscribe(new SimpleSubscription().onRequest((subscription, l) -> {
                try {
                    while (l > 0) {
                        if (subscription.isCancelled()) {
                            break;
                        }

                        String respJson = reader.readLine();

                        if (respJson == null) {
                            break;
                        }

                        if (respJson.length() == 0) {
                            continue;
                        }

                        if (log.isTraceEnabled()) {
                            log.trace("ai-response: {}", respJson);
                        }

                        resp.reset();
                        if (dialect.parseResponseJson(config, resp, respJson)) {
                            if (resp.getError() != null) {
                                subscriber.onError(resp.getError());
                                return;
                            }

                            if (resp.hasChoices()) {
                                AssistantMessage choiceMessage = resp.getMessage();
                                if (Utils.isNotEmpty(choiceMessage.getToolCalls())) {
                                    messages.add(choiceMessage);
                                    buildToolMessage(choiceMessage);

                                    //空读一行（流读取时，一行a）
                                    reader.readLine();

                                    stream().subscribe(subscriber);
                                    return;
                                }

                                if (choiceMessage != null) {
                                    subscriber.onNext(resp);
                                }
                            }

                            if (resp.isFinished()) {
                                subscriber.onComplete();
                                break;
                            } else {
                                l--;
                            }
                        }
                    }
                } catch (Throwable err) {
                    subscriber.onError(err);
                }
            }));
        }
    }

    private void buildToolMessage(AssistantMessage acm) throws ChatException {
        if (Utils.isEmpty(acm.getToolCalls())) {
            return;
        }

        for (ChatFunctionCall call : acm.getToolCalls()) {
            ChatFunction func = config.getGlobalFunction(call.name());

            if (func == null) {
                func = options.function(call.name());
            }

            if (func != null) {
                try {
                    String content = callFunction(func, call.arguments());
                    messages.add(ChatMessage.ofTool(content, call.name(), call.id()));
                } catch (Throwable ex) {
                    throw new ChatException("The function call failed!", ex);
                }
            }
        }
    }

    /**
     * 调用函数
     */
    private String callFunction(ChatFunction func, Map<String, Object> args) throws Throwable {
        Map<String, Object> argsNew = new HashMap<>();

        ONode argsNode = ONode.load(args);
        for (ChatFunctionParam p1 : func.params()) {
            ONode v1 = argsNode.getOrNull(p1.name());
            if (v1 == null) {
                //null
                argsNew.put(p1.name(), null);
            } else {
                //用 ONode 可以自动转换类型
                argsNew.put(p1.name(), v1.toObject(p1.type()));
            }
        }

        return func.handle(argsNew);
    }
}