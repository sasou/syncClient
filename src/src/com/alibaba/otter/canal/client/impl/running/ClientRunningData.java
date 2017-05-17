package com.alibaba.otter.canal.client.impl.running;

/**
 * client running状态信息
 * 
 * @author gene @ 2017-05-05
 * @version 1.0.0
 */
public class ClientRunningData {

    private short   clientId;
    private String  address;
    private boolean active = true;

    public short getClientId() {
        return clientId;
    }

    public void setClientId(short clientId) {
        this.clientId = clientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
