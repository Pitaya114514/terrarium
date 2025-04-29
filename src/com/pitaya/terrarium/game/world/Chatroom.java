package com.pitaya.terrarium.game.world;

import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    private final List<WorldListener> listeners = new ArrayList<>();
    private final List<String> messageList = new ArrayList<>();

    public void sendMessage(String s) {
        messageList.add(s);
        for (WorldListener listener : listeners) {
            listener.trigger(new WorldEvent(this));
        }
    }

    public void clear() {
        messageList.clear();
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void addListener(WorldListener listener) {
        listeners.add(listener);
    }
}
