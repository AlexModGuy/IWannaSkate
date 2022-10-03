package com.github.alexthe668.iwannaskate.server.entity;

public enum SkaterPose {
    NONE(0.0F),
    PEDAL(0.02F),
    STAND_SIDEWAYS(0.04F),
    CROUCH(0.06F),
    OLLIE(0.04F),
    KICKFLIP(0.04F),
    GRIND(0.04F);

    private float speed;

    SkaterPose(float speed){
        this.speed = speed;
    }

    public boolean isSideways(){
        return this != NONE && this != PEDAL && this != GRIND;
    }

    public boolean useBoardPitch(){
        return this.allowJumping() && this != KICKFLIP;
    }

    public boolean useBoardRoll(){
        return this != KICKFLIP;
    }

    public boolean allowJumping(){
        return this != PEDAL;
    }

    public boolean canBeOverriden(){
        return true;
    }

    public static SkaterPose get(int i) {
        if(i <= 0){
            return SkaterPose.NONE;
        }else{
            return SkaterPose.values()[Math.min(SkaterPose.values().length - 1, i)];
        }
    }

    public float getSpeed(float pedalAmount) {
        if(this == PEDAL){
            return speed + 0.015F * pedalAmount;
        }
        return speed;
    }
}
