package com.github.alexthe668.iwannaskate.server.entity;

public interface SlowableEntity {

    void setTickRate(int tickRate);
    int getTickRate();
    boolean cancelTick(int serverTick);
}
