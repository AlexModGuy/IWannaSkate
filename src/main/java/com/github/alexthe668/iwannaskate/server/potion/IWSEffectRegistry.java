package com.github.alexthe668.iwannaskate.server.potion;


import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSEffectRegistry {

    public static final DeferredRegister<MobEffect> DEF_REG = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, IWannaSkateMod.MODID);

    public static final RegistryObject<MobEffect> HIGH_OCTANE = DEF_REG.register("high_octane", ()-> new HighOctaneEffect());
    public static final RegistryObject<MobEffect> OVERCAFFEINATED = DEF_REG.register("overcaffeinated", ()-> new OvercaffeinatedEffect());

}
