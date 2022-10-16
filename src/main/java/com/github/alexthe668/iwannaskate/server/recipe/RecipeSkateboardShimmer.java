package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Map;

public class RecipeSkateboardShimmer extends CustomRecipe implements SpecialRecipeInGuideBook {


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

    @Override
    public NonNullList<Ingredient> getDisplayIngredients() {
        ItemStack   skateboard = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(skateboard);
        map.putIfAbsent(IWSEnchantmentRegistry.AERIAL.get(), 1);
        EnchantmentHelper.setEnchantments(map, skateboard);
        SkateboardData data = SkateboardData.fromStack(skateboard);
        data.removeGripTape();
        data.removeBanner();
        data.setWheelType(SkateboardWheels.DEFAULT);
        SkateboardData.setStackData(skateboard, data);
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(skateboard), Ingredient.of(IWSItemRegistry.SHIMMERING_WAX.get()));
    }

    @Override
    public ItemStack getDisplayResultFor(NonNullList<ItemStack> nonNullList) {
        return new ItemStack(IWSItemRegistry.SKATEBOARD.get());
    }
}