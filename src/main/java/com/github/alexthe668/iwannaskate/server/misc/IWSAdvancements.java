package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class IWSAdvancements {

    public static final IWSAdvancementTrigger TAKE_SKATE_DAMAGE = new IWSAdvancementTrigger(new ResourceLocation(IWannaSkateMod.MODID, "take_skate_damage"));
    public static final IWSAdvancementTrigger TRICK_OLLIE = new IWSAdvancementTrigger(new ResourceLocation(IWannaSkateMod.MODID, "trick_ollie"));
    public static final IWSAdvancementTrigger TRICK_KICKFLIP = new IWSAdvancementTrigger(new ResourceLocation(IWannaSkateMod.MODID, "trick_kickflip"));
    public static final IWSAdvancementTrigger TRICK_GRIND = new IWSAdvancementTrigger(new ResourceLocation(IWannaSkateMod.MODID, "trick_grind"));
    public static final IWSAdvancementTrigger SLOW_MOTION = new IWSAdvancementTrigger(new ResourceLocation(IWannaSkateMod.MODID, "slow_motion"));

    public static void init(){
        CriteriaTriggers.register(TAKE_SKATE_DAMAGE);
        CriteriaTriggers.register(TRICK_OLLIE);
        CriteriaTriggers.register(TRICK_KICKFLIP);
        CriteriaTriggers.register(TRICK_GRIND);
        CriteriaTriggers.register(SLOW_MOTION);
    }

    public static void trigger(Entity entity, IWSAdvancementTrigger trigger){
        if(entity instanceof ServerPlayer serverPlayer){
            trigger.trigger(serverPlayer);
        }
    }

}
