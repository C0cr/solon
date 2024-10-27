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
package org.noear.solon.sessionstate.redisson;

import org.noear.solon.Utils;
import org.noear.solon.boot.web.SessionStateBase;
import org.noear.solon.core.handle.Context;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 它会是个单例，不能有上下文数据
 * */
public class RedissonSessionState extends SessionStateBase {

    private RedissonClient redisClient;

    protected RedissonSessionState(Context ctx) {
        super(ctx);
        this.redisClient = RedissonSessionStateFactory.getInstance().redisClient();
    }

    //
    // session control
    //

    @Override
    public String sessionId() {
        String _sessionId = ctx.attrOrDefault("sessionId", null);

        if (_sessionId == null) {
            _sessionId = sessionIdGet(false);
            ctx.attrSet("sessionId", _sessionId);
        }

        return _sessionId;
    }

    @Override
    public String sessionChangeId() {
        sessionIdGet(true);
        ctx.attrSet("sessionId", null);
        return sessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        RMapCache<String, Object> hash = redisClient.getMapCache(sessionId());
        return hash.keySet();
        //return redisClient.openAndGet((ru) -> ru.key(sessionId()).hashGetAllKeys());
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        //String json = redisClient.openAndGet((ru) -> ru.key(sessionId()).expire(_expiry).hashGet(key));

        RMapCache<String, Object> hash = redisClient.getMapCache(sessionId());
        return (T) hash.get(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            RMapCache<String, Object> hash = redisClient.getMapCache(sessionId());
            hash.put(key, val, _expiry, TimeUnit.SECONDS);
        }
    }

    @Override
    public void sessionRemove(String key) {
        RMapCache<String, Object> hash = redisClient.getMapCache(sessionId());
        hash.remove(key);
    }

    @Override
    public void sessionClear() {
        redisClient.getMapCache(sessionId()).delete();
    }

    @Override
    public void sessionReset() {
        sessionClear();
        sessionChangeId();
    }

    @Override
    public void sessionRefresh() {
        String sid = sessionIdPush();

        if (Utils.isEmpty(sid) == false) {
            RMapCache<String, Object> hash = redisClient.getMapCache(sessionId());
            hash.expire(_expiry, TimeUnit.SECONDS);
        }
    }


    @Override
    public boolean replaceable() {
        return false;
    }
}
