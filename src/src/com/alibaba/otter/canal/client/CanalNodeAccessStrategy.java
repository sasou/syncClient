package com.alibaba.otter.canal.client;

import java.net.SocketAddress;

/**
 * 集群节点访问控制接口
 * 
 * @author gene @ 2017-05-05
 * @version 1.0.0
 */
public interface CanalNodeAccessStrategy {

    SocketAddress currentNode();

    SocketAddress nextNode();
}
