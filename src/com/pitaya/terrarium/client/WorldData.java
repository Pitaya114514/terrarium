package com.pitaya.terrarium.client;

public final class WorldData {
    public String name;
    public String data;

    public WorldData(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return name;
    }
}
