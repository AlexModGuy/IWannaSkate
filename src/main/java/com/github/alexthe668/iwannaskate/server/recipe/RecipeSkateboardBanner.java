package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeSkateboardBanner extends CustomRecipe implements SpecialRecipeInGuideBook {
    public RecipeSkateboardBanner(ResourceLocation name, CraftingBookCategory category) {
        super(name, category);
    }

    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack skateboard = ItemStack.EMPTY;
        ItemStack bannerStack = ItemStack.EMPTY;

        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack2 = craftingContainer.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    if (!bannerStack.isEmpty()) {
                        return false;
                    }

                    bannerStack = itemstack2;
                } else {
                    if (!itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                        return false;
                    }

                    if (!skateboard.isEmpty()) {
                        return false;
                    }

                    if (SkateboardData.fromStack(itemstack2).hasBanner()) {
                        return false;
                    }

                    skateboard = itemstack2;
                }
            }
        }

        return !skateboard.isEmpty() && !bannerStack.isEmpty();
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack banner = ItemStack.EMPTY;
        ItemStack skateboard = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack2 = container.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    banner = itemstack2;
                } else if (itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                    skateboard = itemstack2.copy();
                }
            }
        }

        if (skateboard.isEmpty()) {
            return skateboard;
        } else {
            CompoundTag compoundtag = BlockItem.getBlockEntityData(banner);
            CompoundTag compoundtag1 = compoundtag == null ? new CompoundTag() : compoundtag.copy();
            compoundtag1.putInt("Base", ((BannerItem)banner.getItem()).getColor().getId());

            SkateboardData data = SkateboardData.fromStack(skateboard);
            data.setBanner(compoundtag1);
            SkateboardData.setStackData(skateboard, data);
            return skateboard;
        }
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return IWSRecipeRegistry.SKATEBOARD_BANNER.get();
    }

    @Override
    public NonNullList<Ingredient> getDisplayIngredients() {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(IWSItemRegistry.SKATEBOARD.get()), Ingredient.of(ItemTags.BANNERS));
    }

    @Override
    public ItemStack getDisplayResultFor(NonNullList<ItemStack> nonNullList) {
        ItemStack banner = ItemStack.EMPTY;
        ItemStack skateboard = ItemStack.EMPTY;

        for(int i = 0; i < nonNullList.size(); ++i) {
            ItemStack itemstack2 = nonNullList.get(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    banner = itemstack2;
                } else if (itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                    skateboard = itemstack2.copy();
                }
            }
        }

        if (skateboard.isEmpty()) {
            return skateboard;
        } else {
            CompoundTag compoundtag = BlockItem.getBlockEntityData(banner);
            CompoundTag compoundtag1 = compoundtag == null ? new CompoundTag() : compoundtag.copy();
            compoundtag1.putInt("Base", ((BannerItem)banner.getItem()).getColor().getId());

            SkateboardData data = SkateboardData.fromStack(skateboard);
            data.setBanner(compoundtag1);
            SkateboardData.setStackData(skateboard, data);
            return skateboard;
        }
    }
}