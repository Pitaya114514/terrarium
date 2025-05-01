package com.pitaya.terrarium.game.tool;

import java.util.Timer;
import java.util.TimerTask;

public final class Counter {
    private int count;
    private final Timer timer;
    private int value;

    public Counter() {
        timer = new Timer();
    }

    public void schedule() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                value = count;
                count = 0;
            }
        }, 0, 1000);
    }

    public void cancel() {
        timer.cancel();
    }

    public void addCount() {
        count++;
    }

    public int getValue() {
        return value;
    }
}
