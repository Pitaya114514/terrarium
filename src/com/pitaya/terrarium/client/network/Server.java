package com.pitaya.terrarium.client.network;

import java.net.InetAddress;

public record Server(String name, int port, InetAddress address) {
}
