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
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.cache.caffeine.CaffeineCacheService;
import org.noear.solon.data.cache.CacheService;

import java.util.Properties;

/**
 * @author noear 2022/2/21 created
 */
public class CacheTest {
    @Test
    public void case1() {
        Properties props = new Properties();
        props.setProperty("keyHeader", "test");
        CacheService cacheService = new CaffeineCacheService(props);

        cacheService.store("1", "world", 100);

        assert "world".equals(cacheService.get("1", String.class));

        cacheService.remove("1");

        assert cacheService.get("1", String.class) == null;
    }
}
