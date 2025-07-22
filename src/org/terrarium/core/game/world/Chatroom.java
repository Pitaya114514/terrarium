package org.terrarium.core.game.world;

import org.terrarium.core.game.util.GenericEvent;
import org.terrarium.core.game.util.GenericEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    private final List<GenericEventListener> listeners = new ArrayList<>();
    private final List<String> messageList = new ArrayList<>();

    public void sendMessage(Object sender, String s) {
        if (sender == null) {
            messageList.add(String.format(s));
        } else {
            messageList.add(String.format("%s: %s", sender, s));
        }
        for (GenericEventListener listener : listeners) {
            listener.trigger(new GenericEvent(this));
        }
    }

    public void clear() {
        messageList.clear();
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void addListener(GenericEventListener listener) {
        listeners.add(listener);
    }
}
