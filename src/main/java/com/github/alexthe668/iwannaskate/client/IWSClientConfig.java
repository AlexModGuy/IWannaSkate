package com.github.alexthe668.iwannaskate.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class IWSClientConfig {

    public final ForgeConfigSpec.BooleanValue showInertiaIndicator;
    public final ForgeConfigSpec.BooleanValue hideExperienceBar;
    public final ForgeConfigSpec.IntValue inertiaIndicatorX;
    public final ForgeConfigSpec.IntValue inertiaIndicatorY;
    public final ForgeConfigSpec.BooleanValue flipBoardItems;
    public final ForgeConfigSpec.BooleanValue invertSide;
    public final ForgeConfigSpec.BooleanValue animateAllEntityModels;
    public final ForgeConfigSpec.BooleanValue skateboardLoopSounds;

    public IWSClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("display");
        showInertiaIndicator = builder.comment("when enabled, shows a skateboard icon on the user HUD that indicates their current inertia.").translation("show_inertia_indicator").define("show_inertia_indicator", true);
        hideExperienceBar = builder.comment("when enabled, hides the experience bar when on a skateboard, so that it will not clutter the UI.").translation("hide_experience_bar").define("hide_experience_bar", true);
        inertiaIndicatorX = builder.comment("determines how far to the left the inertia indicator renders on the screen. Negative numbers will render it on the right. ").translation("inertia_indicator_x").defineInRange("inertia_indicator_x", 123, -12000, 12000);
        inertiaIndicatorY = builder.comment("determines how far from bottom the inertia indicator renders on the screen.").translation("inertia_indicator_y").defineInRange("inertia_indicator_y", 12, -12000, 12000);
        flipBoardItems = builder.comment("when enabled, a skateboard with a banner pattern will flip over when the mouse is over it in the inventory.").translation("flip_board_items").define("flip_board_items", true);
        builder.pop();
        builder.push("animation");
        invertSide = builder.comment("when enabled, flips the foot used to pedal to the other side, which is also inverted by default if the player's skin is left handed.").translation("invert_side").define("invert_side", false);
        animateAllEntityModels = builder.comment("when enabled, dynamically identifies the parts of each entity model to animate when skating. Mappings of entities are read from the skate_model_mappings folder in the resource pack. If disabled, only humanoid entities will have animations while skating.").translation("animate_all_entity_models").define("animate_all_entity_models", true);
        builder.pop();
        builder.push("sound");
        skateboardLoopSounds = builder.comment("when enabled, skateboards will play a looping sound whilst moving.").translation("skateboard_loop_sounds").define("skateboard_loop_sounds", true);
        builder.pop();

    }
}
