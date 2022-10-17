package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class IWSTags {

    public static final TagKey<Item> DECK_MATERIALS = registerItemTag("deck_materials");
    public static final TagKey<Item> SAMPLE_COLORS_ON_LOAD = registerItemTag("sample_colors_on_load");
    public static final TagKey<Item> SKATEBOARD_WHEELS = registerItemTag("skateboard_wheels");
    public static final TagKey<Block> GRINDS = registerBlockTag("grinds");
    public static final TagKey<Block> LOW_SKATE_QUALITY = registerBlockTag("low_skate_quality");
    public static final TagKey<Block> MID_SKATE_QUALITY = registerBlockTag("mid_skate_quality");
    public static final TagKey<Block> HIGH_SKATE_QUALITY = registerBlockTag("high_skate_quality");
    public static final TagKey<Block> BEST_SKATE_QUALITY = registerBlockTag("best_skate_quality");
    public static final TagKey<Block> SPAWNS_SKATER_SKELETONS = registerBlockTag("spawns_skater_skeletons");
    public static final TagKey<EntityType<?>> SKILLED_SKATERS = registerEntityType("skilled_skaters");
    public static final TagKey<EntityType<?>> MAINTAINS_SKATEBOARD_DURABILITY = registerEntityType("maintains_skateboard_durability");
    public static final TagKey<EntityType<?>> HAS_ZOMBIE_ARMS = registerEntityType("has_zombie_arms");
    public static final TagKey<EntityType<?>> OVERRIDES_SKATEBOARD_POSITIONING = registerEntityType("overrides_skateboard_positioning");
    public static final TagKey<EntityType<?>> CANNOT_SKATE = registerEntityType("cannot_skate");
    public static final TagKey<Biome> NO_MONSTERS = registerBiome("no_monsters");

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(IWannaSkateMod.MODID, name));
    }

    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(IWannaSkateMod.MODID, name));
    }

    private static TagKey<EntityType<?>> registerEntityType(String name) {
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(IWannaSkateMod.MODID, name));
    }

    private static TagKey<Biome> registerBiome(String name) {
        return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(IWannaSkateMod.MODID, name));
    }
}
