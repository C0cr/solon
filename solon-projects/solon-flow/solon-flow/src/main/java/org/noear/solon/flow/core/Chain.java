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
package org.noear.solon.flow.core;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.driver.SimpleFlowDriver;
import org.noear.solon.lang.Preview;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 链
 *
 * @author noear
 * @since 3.0
 * */
@Preview("3.0")
public class Chain {
    private final String id;
    private final String title;
    private final ChainDriver driver;

    private final Map<String, Node> nodes = new HashMap<>();
    private final List<NodeLink> links = new ArrayList<>();

    private Node start;

    public Chain(String id) {
        this(id, null, null);
    }

    public Chain(String id, String title) {
        this(id, title, null);
    }

    public Chain(String id, String title, ChainDriver driver) {
        this.id = id;
        this.title = (title == null ? id : title);
        this.driver = (driver == null ? SimpleFlowDriver.getInstance() : driver);
    }

    /**
     * 标识
     */
    public String id() {
        return id;
    }

    /**
     * 显示标题
     */
    public String title() {
        return title;
    }

    /**
     * 驱动器
     */
    public ChainDriver driver() {
        return driver;
    }

    /**
     * 获取起始节点
     */
    public Node start() {
        return start;
    }

    /**
     * 获取所有元素
     */
    public Map<String, Node> nodes() {
        return Collections.unmodifiableMap(nodes);
    }

    /**
     * 获取所有连接
     */
    public List<NodeLink> links() {
        return Collections.unmodifiableList(links);
    }


    /**
     * 添加节点
     */
    public void addNode(NodeDecl nodeDecl) {
        List<NodeLink> linkAry = new ArrayList<>();

        for (NodeLinkDecl linkSpec : nodeDecl.links) {
            linkAry.add(new NodeLink(this, nodeDecl.id, linkSpec));
        }

        links.addAll(linkAry);

        Node node = new Node(this, nodeDecl, linkAry);
        nodes.put(node.id(), node);
        if (nodeDecl.type == NodeType.start) {
            start = node;
        }
    }

    /**
     * 获取节点
     */
    public Node getNode(String id) {
        return nodes.get(id);
    }

    /// ////////


    /**
     * 解析文件
     */
    public static Chain parseByUri(String uri) throws IOException {
        URL url = ResourceUtil.findResource(uri, false);
        if (url == null) {
            throw new IllegalArgumentException("Can't find resource: " + uri);
        }

        if (uri.endsWith(".json")) {
            return parseByJson(ResourceUtil.getResourceAsString(url));
        } else if (uri.endsWith(".yml") || uri.endsWith(".yaml") || uri.endsWith(".properties")) {
            return parseByProperties(Utils.loadProperties(url));
        } else {
            throw new IllegalArgumentException("File format is not supported: " + uri);
        }
    }

    /**
     * 解析Json
     */
    public static Chain parseByJson(String json) {
        return parseByDom(ONode.load(json));
    }

    /**
     * 解析属性
     */
    public static Chain parseByProperties(Properties properties) {
        return parseByDom(ONode.load(properties));
    }

    /**
     * 解析文档树
     */
    public static Chain parseByDom(ONode oNode) {
        String id = oNode.get("id").getString();
        String title = oNode.get("id").getString();
        String driverStr = oNode.get("driver").getString();
        ChainDriver driver = (Utils.isEmpty(driverStr) ? null : ClassUtil.tryInstance(driverStr));

        Chain chain = new Chain(id, title, driver);

        for (ONode n1 : oNode.get("nodes").ary()) {
            NodeType type = NodeType.nameOf(n1.get("type").getString());

            NodeDecl nodeDecl = new NodeDecl(n1.get("id").getString(), type);

            nodeDecl.title(n1.get("title").getString());
            nodeDecl.meta(n1.get("meta").toObject(Map.class));
            nodeDecl.task(n1.get("task").getString());

            ONode linkNode = n1.get("link");
            if (linkNode.isArray()) {
                //数组模式（多个）
                for (ONode l1 : linkNode.ary()) {
                    if (l1.isObject()) {
                        //对象模式
                        addLink(nodeDecl, l1);
                    } else if (l1.isValue()) {
                        //单值模式
                        nodeDecl.link(l1.getString());
                    }
                }
            } else if (linkNode.isObject()) {
                //对象模式（单个）
                addLink(nodeDecl, linkNode);
            } else if (linkNode.isValue()) {
                //单值模式（单个）
                nodeDecl.link(linkNode.getString());
            }

            chain.addNode(nodeDecl);
        }

        return chain;
    }

    private static void addLink(NodeDecl nodeDecl, ONode l1) {
        nodeDecl.link(l1.get("toId").getString(), ld -> ld
                .title(l1.get("title").getString())
                .meta(l1.get("meta").toObject(Map.class))
                .condition(l1.get("condition").getString()));
    }
}