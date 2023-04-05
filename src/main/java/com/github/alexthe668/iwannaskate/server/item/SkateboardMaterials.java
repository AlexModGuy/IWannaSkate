package com.github.alexthe668.iwannaskate.server.item;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

public class SkateboardMaterials {

    private static Set<Item> SKATEBOARD_MATERIALS = null;

    public static void reload(){
        IWannaSkateMod.LOGGER.info("reloading skateboard materials");
        SKATEBOARD_MATERIALS = ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(IWSTags.DECK_MATERIALS)).collect(ImmutableSet.toImmutableSet());
    }

    public static Set<Item> getSkateboardMaterials(){
        if(SKATEBOARD_MATERIALS == null || SKATEBOARD_MATERIALS.isEmpty()){
            reload();
        }
        return SKATEBOARD_MATERIALS;
    }

    public static boolean isLoaded(){
        return SKATEBOARD_MATERIALS != null && !SKATEBOARD_MATERIALS.isEmpty();
    }

    public static Set<Item> getSkateboardWheels(){
        return ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(IWSTags.SKATEBOARD_WHEELS)).collect(ImmutableSet.toImmutableSet());
    }

    public static Set<Item> getGrips(){
        return ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(ItemTags.WOOL_CARPETS)).collect(ImmutableSet.toImmutableSet());
    }

    public static Set<Item> getBanners(){
        return ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(ItemTags.BANNERS)).collect(ImmutableSet.toImmutableSet());
    }

    public static SkateboardData generateRandomData(Set<Item> setOfMaterials, RandomSource random, boolean onlyWood){
        List<Item> materials = setOfMaterials.stream().toList();
        Item material;
        if(materials.isEmpty()){
            material = Items.OAK_SLAB;
        }else{
            material = materials.get(materials.size() > 1 ? random.nextInt(materials.size() - 1) : 0);
        }
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(material));
        if(!onlyWood){
            if(random.nextInt(17) != 0){
                data.setGripTape(DyeColor.values()[random.nextInt(DyeColor.values().length - 1)]);
            }
            data.setWheelType(SkateboardWheels.values()[random.nextInt(SkateboardWheels.values().length - 1)]);
            if(random.nextInt(5) != 0) {
                List<Item> banners = getBanners().stream().toList();
                if(!banners.isEmpty()){
                    Item banner = banners.get(banners.size() > 1 ? random.nextInt(banners.size() - 1) : 0);
                    CompoundTag bannerTag = new CompoundTag();
                    bannerTag.putInt("Base", ((BannerItem)banner).getColor().getId());
                    data.setBanner(bannerTag);
                }
            }
        }
        return data;
    }
}
