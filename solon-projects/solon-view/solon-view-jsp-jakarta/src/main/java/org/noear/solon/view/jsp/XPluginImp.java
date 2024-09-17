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
package org.noear.solon.view.jsp;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        if (ClassUtil.loadClass("jakarta.servlet.ServletResponse") == null) {
            LogUtil.global().warn("View: jakarta.servlet.ServletResponse not exists! JspRender failed to load.");
            return;
        }

        JspRender render = JspRender.global();

        Solon.app().renderManager().register(render);
        Solon.app().renderManager().mapping(".jsp", render);
    }
}
