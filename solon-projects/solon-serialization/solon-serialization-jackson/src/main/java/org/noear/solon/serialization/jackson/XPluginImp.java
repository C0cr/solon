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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Constants;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.jackson.impl.NullValueSerializerImpl;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import static com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        JsonProps jsonProps = JsonProps.create(context);

        //::renderFactory
        //绑定属性
        JacksonRenderFactory renderFactory = new JacksonRenderFactory();
        applyProps(renderFactory, jsonProps);

        //事件扩展
        context.wrapAndPut(JacksonRenderFactory.class, renderFactory);

        //::renderTypedFactory
        JacksonRenderTypedFactory renderTypedFactory = new JacksonRenderTypedFactory();
        context.wrapAndPut(JacksonRenderTypedFactory.class, renderTypedFactory);


        context.lifecycle(Constants.LF_IDX_PLUGIN_BEAN_USES, () -> {
            RenderManager.mapping("@json", renderFactory.create());
            RenderManager.mapping("@type_json", renderTypedFactory.create());
        });

        //支持 json 内容类型执行
        JacksonActionExecutor actionExecutor = new JacksonActionExecutor();
        context.wrapAndPut(JacksonActionExecutor.class, actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }

    private void applyProps(JacksonRenderFactory factory, JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(factory, jsonProps)) {
            if (jsonProps.longAsString) {
                factory.addConvertor(Long.class, String::valueOf);
                factory.addConvertor(long.class, String::valueOf);
            }

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if (writeNulls) {
//                factory.config()
//                        .getSerializerFactory()
//                        .withSerializerModifier(new NullBeanSerializerModifierImpl(jsonProps));
                factory.config()
                        .getSerializerProvider()
                        .setNullValueSerializer(new NullValueSerializerImpl(jsonProps));
            }

            if(jsonProps.enumAsName){
                factory.config().configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING,true);
            }
        }

        if (writeNulls == false) {
            factory.config().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        //启用 transient 关键字
        factory.config().configure(PROPAGATE_TRANSIENT_MARKER,true);
        //启用排序（即使用 LinkedHashMap）
        factory.config().configure(SORT_PROPERTIES_ALPHABETICALLY, true);
        //是否识别不带引号的key
        factory.config().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否识别单引号的key
        factory.config().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //浮点数默认类型（dubbod 转 BigDecimal）
        factory.config().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);


        //反序列化时候遇到不匹配的属性并不抛出异常
        factory.config().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化时候遇到空对象不抛出异常
        factory.config().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //反序列化的时候如果是无效子类型,不抛出异常
        factory.config().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }
}
