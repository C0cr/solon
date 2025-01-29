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
package org.noear.solon.web.rx.integration;

import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ClassUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * Action 响应式返回处理
 *
 * @author noear
 * @since 2.3
 */
public class ActionReturnReactiveHandler implements ActionReturnHandler {
    private final boolean hasReactor;

    public ActionReturnReactiveHandler() {
        hasReactor = ClassUtil.hasClass(() -> Flux.class);
    }

    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        return Publisher.class.isAssignableFrom(returnType);
    }

    @Override
    public void returnHandle(Context ctx, Action action, Object result) throws Throwable {
        if (result != null) {
            if (ctx.asyncSupported() == false) {
                throw new IllegalStateException("This boot plugin does not support asynchronous mode");
            }

            if (hasReactor) {
                //reactor 排除也不会出错
                if (result instanceof Flux) {
                    if (ctx.acceptNew().startsWith(MimeType.APPLICATION_X_NDJSON_VALUE) == false) {
                        result = ((Flux) result).collectList();
                    }
                }
            }

            ((Publisher) result).subscribe(new ActionReactiveSubscriber(ctx, action));
        }
    }
}