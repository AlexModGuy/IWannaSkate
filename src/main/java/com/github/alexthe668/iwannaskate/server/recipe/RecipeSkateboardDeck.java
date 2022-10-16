package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeSkateboardDeck  extends ShapedRecipe implements SpecialRecipeInGuideBook {
    public RecipeSkateboardDeck(ResourceLocation name) {
        super(name, "", 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(IWSTags.DECK_MATERIALS), Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.of(IWSTags.DECK_MATERIALS), Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.of(IWSTags.DECK_MATERIALS)), new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get()));
    }

    public boolean matches(CraftingContainer container, Level level) {
        if(super.matches(container, level)){
            ItemStack lastTagged = ItemStack.EMPTY;
            for(int i = 0; i < container.getContainerSize(); i++){
                if(container.getItem(i).is(IWSTags.DECK_MATERIALS)){
                    if(!lastTagged.isEmpty() && !lastTagged.sameItem(container.getItem(i))){
                        return false;
                    }else{
                        lastTagged = container.getItem(i);
                    }
                }
            }
            return true;
        }
        return false;
    }


    public ItemStack getResultItem() {
        return new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get());
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack lastTagged = ItemStack.EMPTY;
        for(int i = 0; i < container.getContainerSize(); i++){
            if(container.getItem(i).is(IWSTags.DECK_MATERIALS)){
                lastTagged = container.getItem(i);
            }
        }
        ItemStack deck = new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get());
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(lastTagged.getItem()));
        CompoundTag deckTag = new CompoundTag();
        deckTag.put("Skateboard", data.toTag());
        deck.setTag(deckTag);
        return deck;
    }

    public RecipeSerializer<?> getSerializer() {
        return IWSRecipeRegistry.SKATEBOARD_DECK.get();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getDisplayIngredients() {
        return getIngredients();
    }

    @Override
    public ItemStack getDisplayResultFor(NonNullList<ItemStack> nonNullList) {
        ItemStack lastTagged = ItemStack.EMPTY;
        for(int i = 0; i < nonNullList.size(); i++){
            if(nonNullList.get(i).is(IWSTags.DECK_MATERIALS)){
                lastTagged = nonNullList.get(i);
            }
        }
        ItemStack deck = new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get());
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(lastTagged.getItem()));
        CompoundTag deckTag = new CompoundTag();
        deckTag.put("Skateboard", data.toTag());
        deck.setTag(deckTag);
        return deck;
    }


}

