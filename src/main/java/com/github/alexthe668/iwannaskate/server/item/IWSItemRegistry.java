package com.github.alexthe668.iwannaskate.server.item;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.misc.IWSCreativeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSItemRegistry {

    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, IWannaSkateMod.MODID);

    public static final RegistryObject<Item> SKATER_SKELETON_SPAWN_EGG = DEF_REG.register("spawn_egg_skater_skeleton", () -> new ForgeSpawnEggItem(IWSEntityRegistry.SKATER_SKELETON, 0X9D9C9F,0XAD352B, new Item.Properties().tab(IWSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> WANDERING_SKATER_SPAWN_EGG = DEF_REG.register("spawn_egg_wandering_skater", () -> new ForgeSpawnEggItem(IWSEntityRegistry.WANDERING_SKATER, 0XB57B67,0XAD352B, new Item.Properties().tab(IWSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> SKATEBOARD = DEF_REG.register("skateboard", () -> new SkateboardItem(new Item.Properties().tab(IWSCreativeTab.INSTANCE).durability(1000)));
    public static final RegistryObject<Item> SKATEBOARD_DECK = DEF_REG.register("skateboard_deck", () -> new BaseSkateboardItem(new Item.Properties().tab(IWSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> SKATEBOARD_TRUCK = DEF_REG.register("skateboard_truck", () -> new Item(new Item.Properties().tab(IWSCreativeTab.INSTANCE)));
    public static final RegistryObject<Item> SHIMMERING_WAX = DEF_REG.register("shimmering_wax", () -> new SimpleFoiledItem(new Item.Properties().tab(IWSCreativeTab.INSTANCE)));
    static {
        SkateboardWheels.init();
    }

}
