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
package org.noear.solon.net.http;

import org.noear.solon.Solon;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ConsumerEx;

/**
 * 预热工具
 *
 * @author noear
 * @since 2.8
 */
public final class PreheatUtils {

    /**
     * 预热本地地址
     */
    public static void preheat(String path) {
        preheat(path, h -> h.get());
    }

    /**
     * 预热本地地址
     */
    public static void preheat(String path, ConsumerEx<HttpUtils> handling) {
        if (NativeDetector.isAotRuntime()) {
            return;
        }

        try {
            HttpUtils http = HttpUtils.http("http://localhost:" + Solon.cfg().serverPort() + path);
            handling.accept(http);
            System.out.println("[Preheat] " + path + " : preheat succeeded");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
