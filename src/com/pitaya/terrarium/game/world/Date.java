package com.pitaya.terrarium.game.world;

public class Date {
    private int time;
    private int days;
    private boolean isDaytime;

    public Date() {
        isDaytime = true;
        this.time = 13500;
    }

    public void addTime() {
        check();
        time++;
    }

    public void setTime(int time) {
        if (time >= 0) {
            this.time = time;
            check();
        }
    }

    public int getTime() {
        return time;
    }

    public int getDays() {
        return days;
    }

    private void check() {
        if (isDaytime && time >= 54000) {
            isDaytime = false;
            time -= 54000;
        } else if (!isDaytime && time >= 32400) {
            isDaytime = true;
            time -= 32400;
            days++;
        }
    }

    @Override
    public String toString() {
        return String.format("On Day %s at %s", days, time);
    }

    public boolean isDaytime() {
        return isDaytime;
    }

    public void setDays(int days) {
        if (days >= 0) {
            this.days = days;
        }
    }

    public void setDaytime(boolean daytime) {
        isDaytime = daytime;
    }
}
