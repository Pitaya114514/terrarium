package com.pitaya.terrarium.game.packet;

import java.nio.ByteBuffer;

public class Packet {
    protected final ByteBuffer packetBuffer;
    protected final Object[] objectData = new Object[30];
    protected final byte[] byteData;

    public Packet(int capacity) {
        byteData = new byte[capacity];
        packetBuffer = ByteBuffer.wrap(byteData);
        objectData[0] = Packets.EMPTY_PACKET;
    }

    public ByteBuffer pack() {
        return packetBuffer.putInt((Integer) objectData[0]);
    }

    public Object[] unpack() {
        return objectData;
    }
}
