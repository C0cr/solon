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
package org.noear.solon.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.data.cache.CacheService;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Caffeine 封装的缓存服务
 *
 * @author noear
 * @since 1.9
 */
public class CaffeineCacheService implements CacheService {
    private String _cacheKeyHead;
    private int _defaultSeconds;

    private final Cache<String, Object> client;

    public CaffeineCacheService(Cache<String, Object> client, int defSeconds) {
        this(client, null, defSeconds);
    }


    public CaffeineCacheService(Cache<String, Object> client, String keyHeader, int defSeconds) {
        this.client = client;

        if (Utils.isEmpty(keyHeader)) {
            keyHeader = Solon.cfg().appName();
        }

        if (defSeconds < 1) {
            defSeconds = 30;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;
    }

    public CaffeineCacheService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public CaffeineCacheService(Properties prop, String keyHeader, int defSeconds) {
        String defSeconds_str = prop.getProperty("defSeconds");

        if (defSeconds == 0) {
            if (Utils.isNotEmpty(defSeconds_str)) {
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        if (Utils.isEmpty(keyHeader)) {
            keyHeader = Solon.cfg().appName();
        }

        if (defSeconds < 1) {
            defSeconds = 30;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        client = Caffeine.newBuilder()
                .expireAfterWrite(_defaultSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        client.put(key, obj);
    }

    @Override
    public void remove(String key) {
        client.put(key, null);
    }

    @Override
    public <T> T get(String key, Type type) {
        return (T) client.getIfPresent(key);
    }

    @Override
    public <T> T getOrStore(String key, Type type, int seconds, Supplier<T> supplier) {
        return (T) client.get(key, (k) -> supplier.get());
    }
}
