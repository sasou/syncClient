package com.alibaba.otter.canal.client.impl.running;

import java.net.InetSocketAddress;

/**
 * 触发一下mainstem发生切换
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public interface ClientRunningListener {

    /**
     * 触发现在轮到自己做为active，需要载入上一个active的上下文数据
     */
    public InetSocketAddress processActiveEnter();

    /**
     * 触发一下当前active模式失败
     */
    public void processActiveExit();

}