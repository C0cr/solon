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
package features.test2;

import features.model.OrderDo;
import features.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.fastjson.FastjsonRenderFactory;
import org.noear.solon.test.SolonTest;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 时间进行格式化 + long,int 转为字符串
 */
@Import(profiles = "classpath:features2_test2.yml")
@SolonTest
public class TestQuickConfig {
    @Inject
    FastjsonRenderFactory renderFactory;

    @Test
    public void hello2() throws Throwable{
        UserDo userDo = new UserDo();

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date(1673861993477L));
        data.put("long", 12L);
        data.put("int", 12);
        data.put("null", null);

        userDo.setMap1(data);

        ContextEmpty ctx = new ContextEmpty();
        renderFactory.create().render(userDo, ctx);
        String output = ctx.attr("output");

        System.out.println(output);

        assert ONode.load(output).count() == 5;

        //完美
        assert "{\"b1\":true,\"d1\":1.0,\"map1\":{\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12},\"n1\":\"1\",\"s1\":\"noear\"}".equals(output);
    }


    @Test
    public void hello3() throws Throwable{
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("long", 1l);
        data.put("order", new OrderDo());

        ContextEmpty ctx = new ContextEmpty();
        renderFactory.create().render(data, ctx);
        String output = ctx.attr("output");

        System.out.println(output);

        assert "{\"long\":\"1\",\"order\":{\"orderId\":\"2\"}}".equals(output);
    }
}
