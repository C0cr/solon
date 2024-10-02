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
package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.serialization.jackson.impl.TypeReferenceImpl;

import java.util.Collection;
import java.util.List;

/**
 * @author noear
 * @since 1.2
 * */
public class JacksonActionExecutor extends ActionExecuteHandlerDefault {
    private JacksonStringSerializer serializer = new JacksonStringSerializer();

    /**
     * 获取序列化接口
     */
    public JacksonStringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 反序列化配置
     */
    public ObjectMapper config(){
        return serializer.getConfig();
    }

    /**
     * 配置
     */
    public void config(ObjectMapper objectMapper){
        serializer.setConfig(objectMapper);
    }

    public JacksonActionExecutor() {
        serializer.getConfig().enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serializer.getConfig().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        serializer.getConfig().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        serializer.getConfig().activateDefaultTypingAsProperty(
                serializer.getConfig().getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        // 注册 JavaTimeModule ，以适配 java.time 下的时间类型
        serializer.getConfig().registerModule(new JavaTimeModule());
        // 允许使用未带引号的字段名
        serializer.getConfig().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        serializer.getConfig().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        return serializer.matched(ctx, mime);
    }

    /**
     * 转换 body
     *
     * @param ctx   请求上下文
     * @param mWrap 函数包装器
     */
    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        return serializer.deserializeFromBody(ctx);
    }

    /**
     * 转换 value
     *
     * @param ctx     请求上下文
     * @param p       参数包装器
     * @param pi      参数序位
     * @param pt      参数类型
     * @param bodyObj 主体对象
     * @since 1.11 增加 requireBody 支持
     */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if(p.spec().isRequiredPath() || p.spec().isRequiredCookie() || p.spec().isRequiredHeader()){
            //如果是 path、cookie, header
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (p.spec().isRequiredBody() == false && ctx.paramMap().containsKey(p.spec().getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        JsonNode tmp = (JsonNode) bodyObj;

        if (tmp.isObject()) {
            if (p.spec().isRequiredBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.has(p.spec().getName())) {
                    JsonNode m1 = tmp.get(p.spec().getName());

                    return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(m1), new TypeReferenceImpl<>(p));
                }
            }

            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                if (List.class.isAssignableFrom(pt)) {
                    return null;
                }

                if (pt.isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(tmp), new TypeReferenceImpl<>(p));
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(tmp), new TypeReferenceImpl<>(p));
        }

        //return tmp.val().getRaw();
        if (tmp.isValueNode()) {
            return serializer.getConfig().readValue(serializer.getConfig().treeAsTokens(tmp), new TypeReferenceImpl<>(p));
        } else {
            return null;
        }
    }
}
