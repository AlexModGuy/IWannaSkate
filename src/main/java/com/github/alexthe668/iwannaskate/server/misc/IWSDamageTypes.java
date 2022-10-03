package com.github.alexthe668.iwannaskate.server.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class IWSDamageTypes {

    public static final DamageSource SKATE_DAMAGE = new DamageSourceRandomMessages("skate").setIsFall().damageHelmet();

    private static class DamageSourceRandomMessages extends DamageSource {

        public DamageSourceRandomMessages(String message) {
            super(message);
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(4);
            String s = "death.attack." + this.msgId + "_" + type;
            return Component.translatable(s, attacked.getDisplayName());
        }
    }
}
