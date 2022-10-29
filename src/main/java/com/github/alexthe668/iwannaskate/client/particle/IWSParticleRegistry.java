package com.github.alexthe668.iwannaskate.client.particle;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IWannaSkateMod.MODID);

    public static final RegistryObject<SimpleParticleType> HALLOWEEN = DEF_REG.register("halloween", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BEE = DEF_REG.register("bee", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> HOVER = DEF_REG.register("hover", ()-> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SPARKLE = DEF_REG.register("sparkle", ()-> new SimpleParticleType(false));

}
