package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSSoundRegistry {
    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, IWannaSkateMod.MODID);

    public static final RegistryObject<SoundEvent> SKATEBOARD_PEDAL = DEF_REG.register("skateboard_pedal", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_pedal")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_ROUGH_ROLLING_LOOP = DEF_REG.register("skateboard_rough_rolling_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_rough_rolling_loop")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_SMOOTH_ROLLING_LOOP = DEF_REG.register("skateboard_smooth_rolling_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_smooth_rolling_loop")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_GRIND_LOOP = DEF_REG.register("skateboard_grind_loop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_grind_loop")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_CHANGE_POSE = DEF_REG.register("skateboard_change_pose", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_change_pose")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_JUMP_START = DEF_REG.register("skateboard_jump_start", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_jump_start")));
    public static final RegistryObject<SoundEvent> SKATEBOARD_JUMP_LAND = DEF_REG.register("skateboard_jump_land", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skateboard_jump_land")));
    public static final RegistryObject<SoundEvent> SKATER_SKELETON_IDLE = DEF_REG.register("skater_skeleton_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skater_skeleton_idle")));
    public static final RegistryObject<SoundEvent> SKATER_SKELETON_WALK = DEF_REG.register("skater_skeleton_walk", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skater_skeleton_walk")));
    public static final RegistryObject<SoundEvent> SKATER_SKELETON_HURT = DEF_REG.register("skater_skeleton_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skater_skeleton_hurt")));
    public static final RegistryObject<SoundEvent> SKATER_SKELETON_DIE = DEF_REG.register("skater_skeleton_die", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "skater_skeleton_die")));
    public static final RegistryObject<SoundEvent> WANDERING_SKATER_IDLE = DEF_REG.register("wandering_skater_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "wandering_skater_idle")));
    public static final RegistryObject<SoundEvent> WANDERING_SKATER_HURT = DEF_REG.register("wandering_skater_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "wandering_skater_hurt")));
    public static final RegistryObject<SoundEvent> WANDERING_SKATER_DIE = DEF_REG.register("wandering_skater_die", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "wandering_skater_die")));
    public static final RegistryObject<SoundEvent> WANDERING_SKATER_YES = DEF_REG.register("wandering_skater_yes", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "wandering_skater_yes")));
    public static final RegistryObject<SoundEvent> WANDERING_SKATER_MAYBE = DEF_REG.register("wandering_skater_maybe", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "wandering_skater_maybe")));
    public static final RegistryObject<SoundEvent> WANDERING_SKATER_NO = DEF_REG.register("wandering_skater_no", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IWannaSkateMod.MODID, "wandering_skater_no")));

}
