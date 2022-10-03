package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;

public class RecipeSkateboardGripTape extends CustomRecipe {

    public RecipeSkateboardGripTape(ResourceLocation name) {
        super(name);
    }

    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack skateboard = ItemStack.EMPTY;
        ItemStack carpetStack = ItemStack.EMPTY;

        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack2 = craftingContainer.getItem(i);
            if (!itemstack2.isEmpty()) {
                Block block = Block.byItem(itemstack2.getItem());
                if (block instanceof WoolCarpetBlock) {
                    if (!carpetStack.isEmpty()) {
                        return false;
                    }
                    carpetStack = itemstack2;
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

        return !skateboard.isEmpty() && !carpetStack.isEmpty();
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack carpet = ItemStack.EMPTY;
        ItemStack skateboard = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack2 = container.getItem(i);
            if (!itemstack2.isEmpty()) {
                Block block = Block.byItem(itemstack2.getItem());
                if (block instanceof WoolCarpetBlock) {
                    carpet = itemstack2;
                } else if (itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                    skateboard = itemstack2.copy();
                }
            }
        }

        if (skateboard.isEmpty()) {
            return skateboard;
        } else {
            SkateboardData data = SkateboardData.fromStack(skateboard);
            if(Block.byItem(carpet.getItem()) instanceof WoolCarpetBlock carpetBlock){
                data.setGripTape(carpetBlock.getColor());
            }
            SkateboardData.setStackData(skateboard, data);
            return skateboard;
        }
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return IWSRecipeRegistry.SKATEBOARD_GRIP_TAPE.get();
    }
}