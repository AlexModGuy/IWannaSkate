package com.github.alexthe668.iwannaskate.server.entity.ai;

import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.entity.SkaterPose;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class SkaterMoveControl extends MoveControl {

    private final MoveControl actual;
    private int pedalFor;
    private int jumpCooldown;

    public SkaterMoveControl(Mob mob, MoveControl actual) {
        super(mob);
        this.actual = actual;
    }

    public void tick() {
        if(mob.getVehicle() instanceof SkateboardEntity skateboard){
            boolean decreaseSpeedNaturally = true;
            if(this.operation == Operation.MOVE_TO){
                Vec3 wanted = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
                Vec3 sub = wanted.subtract(skateboard.front.position());
                float targetYRot = (float)(Mth.atan2(sub.z, sub.x) * (double)(180F / (float)Math.PI)) - 90.0F;
                float currentYRot =  skateboard.getYRot();
                float sidewinder = skateboard.getEnchantLevel(IWSEnchantmentRegistry.SIDEWINDER.get()) + 1;
                float diff = Mth.wrapDegrees(360 + targetYRot - currentYRot);
                if(Math.abs(diff) > 15){
                    int skill = this.isSkilled() ? 15 : 0;
                    skateboard.setZRot(skateboard.approachRotation(skateboard.getZRot(), Math.signum(diff) * (35 + skill), 5));
                }
                if(sub.length() < 1 || sub.length() < 4 && mob.getRandom().nextInt(50) == 0){
                    this.operation = Operation.WAIT;
                }
                if(skateboard.getForwards() < skateboard.getMaxForwardsTicks() * 0.2F){
                    this.pedalFor = isSkilled() ? 60 :  60 + this.mob.getRandom().nextInt(120);
                }
                if (skateboard.isGrinding()) {
                    skateboard.setSkaterPose(SkaterPose.GRIND);
                } else if(pedalFor > 0){
                    decreaseSpeedNaturally = false;
                    skateboard.setStopMovementFlag(false);
                    skateboard.setSkaterPose(SkaterPose.PEDAL);
                    skateboard.setPedalAmount(Math.min(skateboard.getPedalAmount() + 0.1F, 1F));
                    skateboard.setForwards(Math.min(skateboard.getForwards() + (isSkilled() ? 2 + skateboard.getPedallingAddition() : skateboard.getPedallingAddition()), skateboard.getMaxForwardsTicks()));
                }else if(skateboard.getForwards() > skateboard.getMaxForwardsTicks() * 0.65F && this.isSkilled()){
                    skateboard.setPedalAmount(0);
                    skateboard.setStopMovementFlag(false);
                    if(!skateboard.trickFlag){
                        skateboard.setSkaterPose(SkaterPose.CROUCH);
                    }
                }else{
                    skateboard.setStopMovementFlag(false);
                    if(!skateboard.trickFlag) {
                        skateboard.setSkaterPose(SkaterPose.STAND_SIDEWAYS);
                    }
                }
                if(skateboard.canJump()){
                    if(skateboard.horizontalCollision && jumpCooldown == 0){
                        skateboard.handleStartJump((int) Math.min(sub.y * 50, 100));
                        jumpCooldown = 20 + mob.getRandom().nextInt(50);
                    }else if((mob.getRandom().nextInt(isSkilled() ? 30 : 250) == 0) && jumpCooldown == 0){
                        skateboard.handleStartJump((int) Math.min(20 + mob.getRandom().nextInt(isSkilled() ? 80 : 20), 100));
                        jumpCooldown =  isSkilled() ? 20 + mob.getRandom().nextInt(40) : 100 + mob.getRandom().nextInt(100);
                    }
                }
            }else if(operation == Operation.WAIT){
                skateboard.setSkaterPose(SkaterPose.PEDAL);
                skateboard.setForwards(Math.max(skateboard.getForwards() - 3, 0));
                skateboard.setStopMovementFlag(true);
                if(skateboard.getDeltaMovement().length() < 0.03F){
                    skateboard.setXRot(skateboard.approachRotation(skateboard.getXRot(), -60, 30F));
                    skateboard.setYRot(skateboard.approachRotation(skateboard.getYRot(), mob.yHeadRot, 15F));
                    skateboard.setPedalAmount(0);
                }
                decreaseSpeedNaturally = false;
                skateboard.setPedalAmount(Math.max(skateboard.getPedalAmount() - 0.1F, 0F));
            }
            if(decreaseSpeedNaturally){
                skateboard.setForwards(Math.max(skateboard.getForwards() - skateboard.getForwardsDecay(), 0));

            }
            if(pedalFor > 0){
                pedalFor--;
            }
            if(jumpCooldown > 0){
                jumpCooldown--;
            }
        }else{
            mob.moveControl = actual;
        }
    }

    private boolean isSkilled(){
        return this.mob.getType().is(IWSTags.SKILLED_SKATERS);
    }
}