package com.pitaya.terrarium.game.packet;

import com.pitaya.terrarium.game.entity.life.PlayerEntity;

import java.nio.ByteBuffer;

public class LoginPacket extends Packet {

    public LoginPacket(PlayerEntity player) {
        super(2);
        objectData[0] = Packets.LOGIN_PACKET;
        objectData[1] = player.name;
    }

    @Override
    public ByteBuffer pack() {
        return super.pack().put(((String) objectData[1]).getBytes());
    }

    @Override
    public Object[] unpack() {
        Object[] unpacked = super.unpack();
        return unpacked;
    }
}
