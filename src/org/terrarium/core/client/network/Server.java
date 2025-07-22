package org.terrarium.core.client.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Server {
    private String name;
    private final InetSocketAddress address;

    public Server(String name, InetAddress address, int port) {
        this.name = name;
        this.address = new InetSocketAddress(address, port);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + "(" + address + ")";
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
