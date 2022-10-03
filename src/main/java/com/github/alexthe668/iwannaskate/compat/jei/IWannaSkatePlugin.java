package com.github.alexthe668.iwannaskate.compat.jei;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.recipe.IWSRecipeRegistry;
import com.github.alexthe668.iwannaskate.server.recipe.RecipeSkateboard;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class IWannaSkatePlugin implements IModPlugin {
    public static final ResourceLocation MOD = new ResourceLocation("iwannaskate:iwannaskate");

    @Override
    public ResourceLocation getPluginUid() {
        return MOD;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeTypes.CRAFTING, IWSRecipeMaker.createDeckRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, IWSRecipeMaker.createSkateboardRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, IWSRecipeMaker.createSkateboardBannerRecipes());
        registration.addRecipes(RecipeTypes.CRAFTING, IWSRecipeMaker.createSkateboardGripRecipes());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(IWSItemRegistry.SKATEBOARD_DECK.get(), SkateboardSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(IWSItemRegistry.SKATEBOARD.get(), SkateboardSubtypeInterpreter.INSTANCE);
    }
}
