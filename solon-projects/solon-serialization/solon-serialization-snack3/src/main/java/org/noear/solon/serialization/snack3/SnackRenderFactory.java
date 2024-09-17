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
package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class SnackRenderFactory extends SnackRenderFactoryBase {
    private final Options config;

    public SnackRenderFactory(JsonProps jsonProps) {
        config = Options.def();
        applyProps(jsonProps);
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{"@json"};
    }

    /**
     * 创建
     */
    @Override
    public Render create() {
        SnackStringSerializer serializer = new SnackStringSerializer();
        serializer.setConfig(config);

        return new StringSerializerRender(false, serializer);
    }

    /**
     * 序列化配置
     */
    @Override
    public Options config() {
        return config;
    }

    /**
     * 重新设置特性
     */
    public void setFeatures(Feature... features) {
        config.setFeatures(features);
    }

    /**
     * 添加特性
     */
    public void addFeatures(Feature... features) {
        config.add(features);
    }

    /**
     * 移除特性
     */
    public void removeFeatures(Feature... features) {
        config.remove(features);
    }

    protected void applyProps(JsonProps jsonProps) {
        if (JsonPropsUtil.apply(this, jsonProps)) {
            if (jsonProps.longAsString) {
                this.addConvertor(Long.class, String::valueOf);
                this.addConvertor(long.class, String::valueOf);
            }

            if (jsonProps.nullStringAsEmpty) {
                this.addFeatures(Feature.StringNullAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                this.addFeatures(Feature.BooleanNullAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                this.addFeatures(Feature.NumberNullAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                this.addFeatures(Feature.ArrayNullAsEmpty);
            }

            if (jsonProps.nullAsWriteable) {
                this.addFeatures(Feature.SerializeNulls);
            }

            if (jsonProps.enumAsName) {
                this.addFeatures(Feature.EnumUsingName);
            }
        }
    }
}
