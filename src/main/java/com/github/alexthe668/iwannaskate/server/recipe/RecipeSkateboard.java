package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
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

public class RecipeSkateboard extends ShapedRecipe {
    public RecipeSkateboard(ResourceLocation name) {
        super(name, "", 3, 2, NonNullList.of(Ingredient.EMPTY, Ingredient.of(IWSItemRegistry.SKATEBOARD_TRUCK.get()), Ingredient.of(IWSItemRegistry.SKATEBOARD_DECK.get()), Ingredient.of(IWSItemRegistry.SKATEBOARD_TRUCK.get()), Ingredient.of(IWSTags.SKATEBOARD_WHEELS), Ingredient.EMPTY, Ingredient.of(IWSTags.SKATEBOARD_WHEELS)), new ItemStack(IWSItemRegistry.SKATEBOARD.get()));
    }

    public boolean matches(CraftingContainer container, Level level) {
        if (super.matches(container, level)) {
            ItemStack wheels1 = ItemStack.EMPTY;
            ItemStack wheels2 = ItemStack.EMPTY;
            for (int i = 0; i <= container.getContainerSize(); ++i) {
                if (!container.getItem(i).isEmpty() && container.getItem(i).is(IWSTags.SKATEBOARD_WHEELS)) {
                    if (wheels1.isEmpty()) {
                        wheels1 = container.getItem(i);
                    } else if (wheels2.isEmpty()) {
                        wheels2 = container.getItem(i);
                    }
                }
            }
            return wheels1.sameItem(wheels2);
        }
        return false;
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack deck = ItemStack.EMPTY;
        ItemStack wheels = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).is(IWSItemRegistry.SKATEBOARD_DECK.get())) {
                deck = container.getItem(i);
            }
            if (container.getItem(i).is(IWSTags.SKATEBOARD_WHEELS)) {
                wheels = container.getItem(i);
            }
        }
        ItemStack board = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        CompoundTag skateDataTag = deck.hasTag() && deck.getTag().contains("Skateboard") ? deck.getTag().getCompound("Skateboard") : new CompoundTag();
        SkateboardData data = SkateboardData.fromTag(skateDataTag);
        data.setWheelType(SkateboardWheels.fromItem(wheels.getItem()));
        CompoundTag deckTag = new CompoundTag();
        deckTag.put("Skateboard", data.toTag());
        board.setTag(deckTag);
        return board;
    }

    public RecipeSerializer<?> getSerializer() {
        return IWSRecipeRegistry.SKATEBOARD.get();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    public boolean isSpecial() {
        return true;
    }

}

