package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IWSRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> DEF_REG = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, IWannaSkateMod.MODID);

    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_DECK = DEF_REG.register("skateboard_deck", () -> new SimpleRecipeSerializer<>(RecipeSkateboardDeck::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD = DEF_REG.register("skateboard", () -> new SimpleRecipeSerializer<>(RecipeSkateboard::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_BANNER = DEF_REG.register("skateboard_banner", () -> new SimpleRecipeSerializer<>(RecipeSkateboardBanner::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_GRIP_TAPE = DEF_REG.register("skateboard_grip_tape", () -> new SimpleRecipeSerializer<>(RecipeSkateboardGripTape::new));
    public static final RegistryObject<RecipeSerializer<?>> SKATEBOARD_SHIMMER = DEF_REG.register("skateboard_shimmer", () -> new SimpleRecipeSerializer<>(RecipeSkateboardShimmer::new));

}
