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
package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2024/5/17 created
 */
@SolonTest(App.class)
public class PropRefTest {
    @Inject("${each.a}")
    String eachA;

    @Inject("${each.b}")
    String eachB;

    @Inject("${each.c}")
    String eachC;

    @Inject("${each.d}")
    String eachD;

    @Inject("${each.e}")
    String eachE;

    @Inject("${GLOBAL_EACH}")
    String globalEach;

    @Test
    public void test() {
        System.out.println("each.a: " + eachA);
        System.out.println("each.b: " + eachB);
        System.out.println("each.c: " + eachC);
        System.out.println("each.d: " + eachD);
        System.out.println("each.e: " + eachE);
        System.out.println("GLOBAL_EACH: " + globalEach);

        System.out.println("Solon.cfg() each.a: " + Solon.cfg().get("each.a"));

        assert "each_test_e_d_c_b_a".equals(eachA);
        assert "each_test_e_d_c_b".equals(eachB);
        assert "each_test_e_d_c".equals(eachC);
        assert "each_test_e_d".equals(eachD);
        assert "each_test_e".equals(eachE);
        assert "each_test".equals(globalEach);
    }
}
