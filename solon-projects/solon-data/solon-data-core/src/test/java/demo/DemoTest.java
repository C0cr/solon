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

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranListener;
import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.TranUtils;

/**
 * @author noear 2022/6/30 created
 */
public class DemoTest {

    //两者效果相同

    public void test1() throws Throwable{
        TranUtils.execute(new TranAnno().readOnly(true),()->{
            //..
        });
    }

    @Tran(readOnly = true)
    public void test2(){
        //..
        TranUtils.listen(new TranListener() {
            @Override
            public void afterCommit() {
                TranListener.super.afterCommit();
            }
        });
    }
}
