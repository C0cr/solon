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
package demo;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.test.SolonTest;

import java.util.List;

/**
 * @author noear 2022/1/18 created
 * @author liao.chunping 2022/08/22
 */
@SolonTest(DemoApp.class)
public class DemoTest extends HttpTester {
    @Test
    public void test() throws Exception {
        HttpResponse response = path("/put").data("id", "12").data("name", "world").exec("post");
        HttpUtils request = path("/get");
        request.header("Cookie", getCookie(response));
        String json = request.get();
        assert json.contains("12");
        assert json.contains("world");

        HttpUtils newRequest = path("/get");
        String newJson = newRequest.get();
        assert !newJson.contains("12");
        assert !newJson.contains("world");
    }

    /**
     * 获取 cookie
     *
     * @param response 响应
     * @return cookie
     */
    private String getCookie(HttpResponse response) {
        List<String> cookies = response.cookies();
        StringBuilder sb = new StringBuilder();
        for (String c1 : cookies) {
            String kv = c1.split(";")[0];
            if (kv.contains("=")) {
                sb.append(kv).append(";");
            }
        }
        return sb.toString();
    }
}
