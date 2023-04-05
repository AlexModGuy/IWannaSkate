package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public class IWSDamageTypes {

    public static final ResourceKey<DamageType> SKATE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(IWannaSkateMod.MODID, "skate"));
    public static DamageSource causeSkateDamage(RegistryAccess registryAccess){
        return new DamageSourceRandomMessages(registryAccess.registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(SKATE_DAMAGE));
    }

    private static class DamageSourceRandomMessages extends DamageSource {

        public DamageSourceRandomMessages(Holder.Reference<DamageType> message) {
            super(message);
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity attacked) {
            int type = attacked.getRandom().nextInt(4);
            String s = "death.attack." + this.getMsgId() + "_" + type;
            return Component.translatable(s, attacked.getDisplayName());
        }
    }
}
