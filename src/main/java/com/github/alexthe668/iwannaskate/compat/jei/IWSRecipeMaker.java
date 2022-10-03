package com.github.alexthe668.iwannaskate.compat.jei;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardMaterials;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class IWSRecipeMaker {

    public static List<CraftingRecipe> createDeckRecipes() {
        return SkateboardMaterials.getSkateboardMaterials().stream().map(woodItem -> createDeckRecipe(woodItem)).toList();
    }

    public static List<CraftingRecipe> createSkateboardRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        SkateboardMaterials.getSkateboardMaterials().stream().forEach(woodItem -> recipes.addAll(createSkateboardRecipesForDeck(woodItem)));
        return recipes;
    }

    public static List<CraftingRecipe> createSkateboardBannerRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        SkateboardMaterials.getSkateboardMaterials().stream().forEach(woodItem -> recipes.addAll(createSkateboardBannerRecipes(woodItem)));
        return recipes;
    }

    public static List<CraftingRecipe> createSkateboardGripRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        SkateboardMaterials.getSkateboardMaterials().stream().forEach(woodItem -> recipes.addAll(createSkateboardGripRecipes(woodItem)));
        return recipes;
    }

    private static List<CraftingRecipe> createSkateboardBannerRecipes(Item woodItem) {
        String group = "jei.skateboard_deck";
        List<CraftingRecipe> recipes = new ArrayList<>();
        ItemStack input = createSkateboardForWood(woodItem);
        for(Item banner : SkateboardMaterials.getBanners()){
            if(banner instanceof BannerItem){
                ItemStack output = input.copy();
                SkateboardData data = SkateboardData.fromStack(input);
                CompoundTag bannerTag = new CompoundTag();
                bannerTag.putInt("Base", ((BannerItem)banner).getColor().getId());
                data.setBanner(bannerTag);
                SkateboardData.setStackData(output, data);
                ResourceLocation id = new ResourceLocation(IWannaSkateMod.MODID, "jei.skateboard_banner_" + ForgeRegistries.ITEMS.getKey(woodItem).getPath() + banner.getDescriptionId());
                NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(banner));
                recipes.add(new ShapelessRecipe(id, group, output, inputs));
            }
        }
        return recipes;
    }

    private static List<CraftingRecipe> createSkateboardGripRecipes(Item woodItem) {
        String group = "jei.skateboard_deck";
        List<CraftingRecipe> recipes = new ArrayList<>();
        ItemStack input = createSkateboardForWood(woodItem);
        for(Item carpet : SkateboardMaterials.getGrips()){
            if(Block.byItem(carpet) instanceof WoolCarpetBlock carpetBlock){
                ItemStack output = input.copy();
                SkateboardData data = SkateboardData.fromStack(input);
                data.setGripTape(carpetBlock.getColor());
                SkateboardData.setStackData(output, data);
                ResourceLocation id = new ResourceLocation(IWannaSkateMod.MODID, "jei.skateboard_grip_" + ForgeRegistries.ITEMS.getKey(woodItem).getPath() + carpet.getDescriptionId());
                NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(carpet));
                recipes.add(new ShapelessRecipe(id, group, output, inputs));
            }
        }
        return recipes;
    }

    private static CraftingRecipe createDeckRecipe(Item deckMaterial) {
        String group = "jei.skateboard_deck";
        ItemStack input = new ItemStack(deckMaterial);
        ItemStack output = createDeckForWood(deckMaterial);
        Ingredient woodIngredient = Ingredient.of(input);
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                Ingredient.EMPTY, Ingredient.EMPTY, woodIngredient,
                Ingredient.EMPTY, woodIngredient, Ingredient.EMPTY,
                woodIngredient, Ingredient.EMPTY, Ingredient.EMPTY
        );
        ResourceLocation id = new ResourceLocation(IWannaSkateMod.MODID, "jei.skateboard_deck_" + ForgeRegistries.ITEMS.getKey(deckMaterial).getPath());
        return new ShapedRecipe(id, group, 3, 3, inputs, output);
    }

    private static List<CraftingRecipe> createSkateboardRecipesForDeck(Item woodItem) {
        List<CraftingRecipe> recipes = new ArrayList<>();
        String group = "jei.skateboard";
        for(Item wheel : SkateboardMaterials.getSkateboardWheels()){
            ItemStack output = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
            SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(woodItem));
            data.setWheelType(SkateboardWheels.fromItem(wheel));
            SkateboardData.setStackData(output, data);
            Ingredient deckIngredient = Ingredient.of(createDeckForWood(woodItem));
            Ingredient truckIngredient = Ingredient.of(IWSItemRegistry.SKATEBOARD_TRUCK.get());
            Ingredient wheelIngredient = Ingredient.of(wheel);
            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                    Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
                    truckIngredient, deckIngredient, truckIngredient,
                    wheelIngredient, Ingredient.EMPTY, wheelIngredient
            );
            ResourceLocation id = new ResourceLocation(IWannaSkateMod.MODID, "jei.skateboard_" + ForgeRegistries.ITEMS.getKey(woodItem).getPath());
            recipes.add(new ShapedRecipe(id, group, 3, 3, inputs, output));
        }
        return recipes;
    }

    private static ItemStack createDeckForWood(Item deck) {
        ItemStack stack = new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get());
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(deck));
        SkateboardData.setStackData(stack, data);
        return stack;
    }

    private static ItemStack createSkateboardForWood(Item deck) {
        ItemStack stack = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(deck));
        SkateboardData.setStackData(stack, data);
        return stack;
    }

}
