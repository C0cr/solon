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
package org.noear.solon.core.util;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板工具
 *
 * @author noear
 * @since 2.8
 */
public class TmplUtil {
    public static String parse(String tmpl, Invocation inv) {
        if (tmpl.indexOf('$') < 0) {
            return tmpl;
        }

        return parse(tmpl, inv.argsAsMap());
    }

    public static String parse(String tmpl, Invocation inv, Object rst) {
        if (tmpl.indexOf('$') < 0) {
            return tmpl;
        }

        Map<String, Object> model = new HashMap<>(inv.argsAsMap());
        model.put("", rst);

        return parse(tmpl, model);
    }

    /**
     * 解析模板
     *
     * <pre><code>name=${name},type={.type}</code></pre>
     *
     * @param tmpl  模板
     * @param model 参数
     */
    public static String parse(String tmpl, Map<String, Object> model) {
        if (tmpl.indexOf('$') < 0) {
            return tmpl;
        }

        StringBuilder str2 = new StringBuilder(tmpl);

        //${name}
        //${.name}
        //${obj.name}
        Matcher m = tmpPattern.matcher(tmpl);
        while (m.find()) {
            String mark = m.group(0);
            String name = m.group(1);

            if (model.containsKey(name)) {
                //说明从输入参数取值
                Object val = model.get(name);
                if(val == null){
                    val = "";
                }

                int idx = str2.indexOf(mark);
                str2 = str2.replace(idx, idx + mark.length(), val.toString());
            } else if (name.indexOf('.') >= 0) {
                //说明要从返回结果取值
                Object obj;
                String fieldKey = null;
                String fieldVal = null;
                {
                    String[] cf = name.split("\\.");
                    obj = model.get(cf[0]);
                    fieldKey = cf[1];
                }

                if (obj != null) {
                    Object valTmp = null;

                    if (obj instanceof Map) {
                        valTmp = ((Map) obj).get(fieldKey);
                    } else {
                        FieldWrap fw = ClassWrap.get(obj.getClass()).getFieldWrap(fieldKey);
                        if (fw == null) {
                            throw new IllegalArgumentException("Missing tmpl parameter (result field): " + name);
                        }

                        valTmp = fw.getValue(obj);
                    }

                    if (valTmp != null) {
                        fieldVal = valTmp.toString();
                    }
                }

                if (fieldVal == null) {
                    fieldVal = "";
                }

                int idx = str2.indexOf(mark);
                str2 = str2.replace(idx, idx + mark.length(), fieldVal);
            } else {
                //如果缺少参数就出异常，容易发现问题
                throw new IllegalArgumentException("Missing tmpl parameter: " + name);
            }
        }


        return str2.toString();
    }

    private static final Pattern tmpPattern = Pattern.compile("\\$\\{(\\w*\\.?\\w+)\\}");
}