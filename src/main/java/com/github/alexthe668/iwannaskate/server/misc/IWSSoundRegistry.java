package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSSoundRegistry {
    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, IWannaSkateMod.MODID);

    public static final RegistryObject<SoundEvent> SKATEBOARD_PEDAL = DEF_REG.register("skateboard_pedal", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_pedal")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_ROUGH_ROLLING_LOOP = DEF_REG.register("skateboard_rough_rolling_loop", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_rough_rolling_loop")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_SMOOTH_ROLLING_LOOP = DEF_REG.register("skateboard_smooth_rolling_loop", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_smooth_rolling_loop")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_GRIND_LOOP = DEF_REG.register("skateboard_grind_loop", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_grind_loop")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_CHANGE_POSE = DEF_REG.register("skateboard_change_pose", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_change_pose")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_JUMP_START = DEF_REG.register("skateboard_jump_start", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_jump_start")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_JUMP_LAND = DEF_REG.register("skateboard_jump_land", () -> new SoundEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_jump_land")));

}
