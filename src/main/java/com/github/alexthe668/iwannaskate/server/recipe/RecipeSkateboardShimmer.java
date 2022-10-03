package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;

public class RecipeSkateboardShimmer extends CustomRecipe {

    public RecipeSkateboardShimmer(ResourceLocation name) {
        super(name);
    }

    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack skateboard = ItemStack.EMPTY;
        ItemStack waxStack = ItemStack.EMPTY;

        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack2 = craftingContainer.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.is(IWSItemRegistry.SHIMMERING_WAX.get())) {
                    if (!waxStack.isEmpty()) {
                        return false;
                    }
                    waxStack = itemstack2;
                } else {
                    if (!itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                        return false;
                    }

                    if (!skateboard.isEmpty()) {
                        return false;
                    }

                    if (SkateboardData.fromStack(itemstack2).hasGripTape()) {
                        return false;
                    }

                    skateboard = itemstack2;
                }
            }
        }

        return !skateboard.isEmpty() && !waxStack.isEmpty();
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack skateboard = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack2 = container.getItem(i);
            if (!itemstack2.isEmpty()) {
                 if (itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                    skateboard = itemstack2.copy();
                }
            }
        }

        if (skateboard.isEmpty()) {
            return skateboard;
        } else {
            CompoundTag tag = skateboard.getOrCreateTag();
            boolean prevRemoveShimmer = tag.getBoolean("RemovedShimmer");
            tag.putBoolean("RemovedShimmer", !prevRemoveShimmer);
            skateboard.setTag(tag);
            return skateboard;
        }
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return IWSRecipeRegistry.SKATEBOARD_SHIMMER.get();
    }
}