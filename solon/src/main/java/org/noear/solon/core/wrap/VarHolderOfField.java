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
package org.noear.solon.core.wrap;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.InjectGather;
import org.noear.solon.core.VarHolder;
import org.noear.solon.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

/**
 * 字段变量容器 临时对象
 *
 * 为了稳藏 FieldWrap 的一些特性，并统一对外接口
 *
 * @author noear
 * @since 1.0
 * */
public class VarHolderOfField implements VarHolder {
    private final FieldWrap fw;
    private final Object obj;
    private final AppContext ctx;
    private Class<?> dependencyType;

    private Object val;
    private boolean required = false;
    private boolean done;
    private InjectGather gather;

    public VarHolderOfField(AppContext ctx, FieldWrap fw, Object obj, InjectGather gather) {
        this.ctx = ctx;
        this.fw = fw;
        this.obj = obj;

        this.gather = gather;
    }

    /**
     * 应用上下文
     * */
    @Override
    public AppContext context() {
        return ctx;
    }

    /**
     * 是否为字段
     * */
    @Override
    public boolean isField() {
        return true;
    }

    /**
     * 泛型（可能为null）
     * */
    @Override
    public @Nullable ParameterizedType getGenericType() {
        return fw.genericType;
    }


    @Override
    public Class<?> getDependencyType() {
        if (dependencyType == null) {
            return getType();
        } else {
            return dependencyType;
        }
    }

    @Override
    public void setDependencyType(Class<?> dependencyType) {
        this.dependencyType = dependencyType;
    }

    /**
     * 获取字段类型
     * */
    @Override
    public Class<?> getType(){
        return fw.type;
    }

    /**
     * 获取所有注解
     * */
    @Override
    public Annotation[] getAnnoS(){
        return fw.annoS;
    }

    /**
     * 获取完整名字
     * */
    @Override
    public String getFullName() {
        return fw.entityClz.getName() + "::" + fw.field.getName();
    }


    /**
     * 设置值
     */
    @Override
    public void setValue(Object val) {
        if (val != null) {
            if (val instanceof BeanWrap.Supplier) {
                val = ((BeanWrap.Supplier) val).get();
            }

            fw.setValue(obj, val, true);

            ctx.aot().registerJdkProxyType(getType(), val);
        }

        this.val = val;
        this.done = true;

        if (gather != null) {
            gather.run();
        }
    }

    @Override
    public void setValueOnly(Object val) {
        setValue(val);
    }

    /**
     * 获取值
     * */
    @Override
    public Object getValue() {
        return val;
    }


    /**
     * 是否为完成的（设置值后即为完成态）
     * */
    public boolean isDone() {
        return done;
    }

    /**
     * 是否必须
     * */
    @Override
    public boolean required() {
        return required;
    }

    /**
     * 设定必须
     * */
    @Override
    public void required(boolean required) {
        this.required = required;
    }
}
