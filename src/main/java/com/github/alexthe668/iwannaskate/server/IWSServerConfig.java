package com.github.alexthe668.iwannaskate.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class IWSServerConfig {

    public final ForgeConfigSpec.BooleanValue spawnSkaterSkeletons;
    public final ForgeConfigSpec.BooleanValue skaterSkeletonsUseSkateboards;
    public final ForgeConfigSpec.DoubleValue skaterSkeletonsHolidayWheelsDropChance;

    public final ForgeConfigSpec.BooleanValue enableSlowMotion;
    public final ForgeConfigSpec.BooleanValue playersSlowMotion;
    public final ForgeConfigSpec.IntValue slowMotionDistance;

    public IWSServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("mobs");
        spawnSkaterSkeletons = builder.comment("when enabled, skater skeletons will spawn where applicable.").translation("spawn_skater_skeletons").define("spawn_skater_skeletons", true);
        skaterSkeletonsUseSkateboards = builder.comment("when enabled, skater skeletons will skate using the skateboards they spawn with or any they pick up.").translation("skater_skeletons_use_skateboards").define("skater_skeletons_use_skateboards", true);
        skaterSkeletonsHolidayWheelsDropChance = builder.comment("the chance that a skater skeleton will drop a spooky or snowy wheel when in the month of its respective holiday. 1.0 = 100% chance, 0.0 = disabled.").translation("skater_skeleton_holiday_wheel_drop_chance").defineInRange("skater_skeleton_holiday_wheel_drop_chance", 0.25D, 0D, 1D);
        builder.pop();
        builder.push("slow-motion");
        enableSlowMotion = builder.comment("when enabled, certain skateboards can cause a slow-motion effect to the entities around them.").translation("slow_motion").define("slow_motion", true);
        playersSlowMotion = builder.comment("when enabled, nearby player entities can be slowed down if someone else is using the slow motion feature.").translation("players_slow_motion").define("players_slow_motion", true);
        slowMotionDistance = builder.comment("determines how far in blocks entities are effected by slow motion.").translation("slow_motion_distance").defineInRange("slow_motion_distance", 30, 1, 2000);
        builder.pop();

    }
}
