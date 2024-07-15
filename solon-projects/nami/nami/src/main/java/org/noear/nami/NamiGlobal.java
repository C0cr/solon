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
package org.noear.nami;

/**
 * Nami 全局设置
 *
 * @author noear
 * @since 1.10
 */
public class NamiGlobal {
    /**
     * 最大连接数
     */
    static int maxConnections = 10000;
    /**
     * 连接超时（单位：秒）
     */
    static int connectTimeout = 10;
    /**
     * 读取超时（单位：秒）
     */
    static int readTimeout = 10;
    /**
     * 写入超时（单位：秒）
     */
    static int writeTimeout = 10;

    /**
     * 最大连接数
     */
    public static int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 连接超时（单位：秒）
     */
    public static int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 读取超时（单位：秒）
     */
    public static int getReadTimeout() {
        return readTimeout;
    }

    /**
     * 写入超时（单位：秒）
     */
    public static int getWriteTimeout() {
        return writeTimeout;
    }

    public static void setMaxConnections(int number) {
        if (number > 0) {
            maxConnections = number;
        }
    }

    public static void setConnectTimeout(int seconds) {
        if (seconds > 0) {
            connectTimeout = seconds;
        }
    }

    public static void setReadTimeout(int seconds) {
        if (seconds > 0) {
            readTimeout = seconds;
        }
    }

    public static void setWriteTimeout(int seconds) {
        if (seconds > 0) {
            writeTimeout = seconds;
        }
    }
}
