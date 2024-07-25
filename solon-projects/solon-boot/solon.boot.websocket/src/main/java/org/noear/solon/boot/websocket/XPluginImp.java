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
package org.noear.solon.boot.websocket;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.WebSocketServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;

import java.net.Inet4Address;

public class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private WsServer _server = null;

    public static String solon_boot_ver() {
        return "org.java_websocket 1.5/" + Solon.version();
    }

    @Override
    public void start(AppContext context) {
        if (Solon.app().enableWebSocket() == false) {
            return;
        }

        context.lifecycle(ServerConstants.SIGNAL_LIFECYCLE_INDEX, () -> {
            start0(Solon.app());
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        WebSocketServerProps props = WebSocketServerProps.getInstance();
        final String _host = props.getHost();
        final int _port = props.getPort();

        long time_start = System.currentTimeMillis();

        if (Utils.isEmpty(_host)) {
            _server = new WsServer(_port);
        } else {
            _server = new WsServer(Inet4Address.getByName(_host), _port);
        }

        _server.setReuseAddr(true); //重启时，端口可立即复用
        _server.start();

        if (Utils.isNotEmpty(props.getName())) {
            _signal = new SignalSim(props.getName(), props.getWrapHost(), props.getWrapPort(), "ws", SignalType.WEBSOCKET);

            app.signalAdd(_signal);
        }

        long time_end = System.currentTimeMillis();

        String wsServerUrl = props.buildWsServerUrl(false);
        LogUtil.global().info("Connector:main: websocket: Started ServerConnector@{HTTP/1.1,[WebSocket]}{" + wsServerUrl + "}");
        LogUtil.global().info("Server:main: websocket: Started (" + solon_boot_ver() + ") @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            LogUtil.global().info("Server:main: websocket: Has Stopped (" + solon_boot_ver() + ")");
        }
    }
}
