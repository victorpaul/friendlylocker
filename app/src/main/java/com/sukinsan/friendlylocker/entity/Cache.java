package com.sukinsan.friendlylocker.entity;

/**
 * Created by victor on 06.08.15.
 */
public class Cache {
    private int delay;
    private boolean playSongOnSensor;
    private boolean vibrateOnSensor;

    public Cache() {
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isPlaySongOnSensor() {
        return playSongOnSensor;
    }

    public void setPlaySongOnSensor(boolean playSongOnSensor) {
        this.playSongOnSensor = playSongOnSensor;
    }

    public boolean isVibrateOnSensor() {
        return vibrateOnSensor;
    }

    public void setVibrateOnSensor(boolean vibrateOnSensor) {
        this.vibrateOnSensor = vibrateOnSensor;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "delay=" + delay +
                ", playSongOnSensor=" + playSongOnSensor +
                ", vibrateOnSensor=" + vibrateOnSensor +
                '}';
    }
}
