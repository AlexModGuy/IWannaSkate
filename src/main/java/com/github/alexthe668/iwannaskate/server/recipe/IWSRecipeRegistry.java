package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class IWSRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> DEF_REG = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, IWannaSkateMod.MODID);

    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_DECK = DEF_REG.register("skateboard_deck", () -> new SimpleRecipeSerializer<>(RecipeSkateboardDeck::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD = DEF_REG.register("skateboard", () -> new SimpleRecipeSerializer<>(RecipeSkateboard::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_BANNER = DEF_REG.register("skateboard_banner", () -> new SimpleRecipeSerializer<>(RecipeSkateboardBanner::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_GRIP_TAPE = DEF_REG.register("skateboard_grip_tape", () -> new SimpleRecipeSerializer<>(RecipeSkateboardGripTape::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_SHIMMER = DEF_REG.register("skateboard_shimmer", () -> new SimpleRecipeSerializer<>(RecipeSkateboardShimmer::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_SWAP_WHEELS = DEF_REG.register("skateboard_swap_wheels", () -> new SimpleRecipeSerializer<>(RecipeSkateboardSwapWheels::new));

    public static void registerCauldronInteractions(){
        Map<DyeColor, ItemLike> dyeToCarpet = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
            map.put(DyeColor.WHITE, Blocks.WHITE_CARPET);
            map.put(DyeColor.ORANGE, Blocks.ORANGE_CARPET);
            map.put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET);
            map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET);
            map.put(DyeColor.YELLOW, Blocks.YELLOW_CARPET);
            map.put(DyeColor.LIME, Blocks.LIME_CARPET);
            map.put(DyeColor.PINK, Blocks.PINK_CARPET);
            map.put(DyeColor.GRAY, Blocks.GRAY_CARPET);
            map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET);
            map.put(DyeColor.CYAN, Blocks.CYAN_CARPET);
            map.put(DyeColor.PURPLE, Blocks.PURPLE_CARPET);
            map.put(DyeColor.BLUE, Blocks.BLUE_CARPET);
            map.put(DyeColor.BROWN, Blocks.BROWN_CARPET);
            map.put(DyeColor.GREEN, Blocks.GREEN_CARPET);
            map.put(DyeColor.RED, Blocks.RED_CARPET);
            map.put(DyeColor.BLACK, Blocks.BLACK_CARPET);
        });

        CauldronInteraction.WATER.put(IWSItemRegistry.SKATEBOARD.get(), (blockState, level, pos, player, hand, stack) -> {
            SkateboardData data = SkateboardData.fromStack(stack);
            boolean used = false;
            if(data.hasGripTape()){
                used = true;
                DyeColor gripTapeColor = data.getGripTapeColor();
                ItemStack itemstack = new ItemStack(dyeToCarpet.get(gripTapeColor));
                if(!itemstack.isEmpty()){
                    if (player.getInventory().add(itemstack)) {
                        player.inventoryMenu.sendAllDataToRemote();
                    } else {
                        player.drop(itemstack, false);
                    }
                }
                data.removeGripTape();
            }else if(data.hasBanner()){
                used = true;
                data.removeBanner();
            }
            if(used){
                SkateboardData.setStackData(stack, data);
                LayeredCauldronBlock.lowerFillLevel(blockState, level, pos);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }else{
                return InteractionResult.PASS;
            }
        });
    }
}
