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


import java.text.ParseException;
import java.util.Date;

/**
 * 日期分析器
 *
 * @author noear
 * @since 1.5
 */
public class DateAnalyzer {
    //
    // 可以进行替换扩展
    //
    private static DateAnalyzer global = new DateAnalyzer();

    public static DateAnalyzer global() {
        return global;
    }

    public static void globalSet(DateAnalyzer instance) {
        if (instance != null) {
            global = instance;
        }
    }

    /**
     * 解析
     */
    public Date parse(String val) throws ParseException {
        return DateUtil.parse(val);
    }
}