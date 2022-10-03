package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;

public class IWSCreativeTab extends CreativeModeTab {
    public static final CreativeModeTab INSTANCE = new IWSCreativeTab();

    public IWSCreativeTab() {
        super(IWannaSkateMod.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        ItemStack stack = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IsCreativeTab", true);
        stack.setTag(tag);
        return stack;
    }

    public void fillItemList(NonNullList<ItemStack> items) {
        super.fillItemList(items);
        for(Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()){
            if(enchantment.category == IWSEnchantmentRegistry.SKATEBOARD){
                items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));

            }
        }
    }
}
