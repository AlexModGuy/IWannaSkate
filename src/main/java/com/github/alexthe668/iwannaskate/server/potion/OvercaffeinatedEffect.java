package com.github.alexthe668.iwannaskate.server.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class OvercaffeinatedEffect extends MobEffect {

    protected OvercaffeinatedEffect() {
        super(MobEffectCategory.HARMFUL, 0X538345);
    }

    public void applyEffectTick(LivingEntity entity, int tick) {
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(0.1F * (float)(tick + 1));
        }
        if(entity.getHealth() > entity.getMaxHealth() * 0.25F && entity.getRandom().nextInt(10) == 0){
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }
    }

    public boolean isDurationEffectTick(int tick1, int tick2) {
        int j = 25 >> tick2;
        if (j > 0) {
            return tick1 % j == 0;
        } else {
            return true;
        }
    }
}
