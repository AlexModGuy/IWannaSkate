package com.github.alexthe668.iwannaskate.server.enchantment;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class SkateboardEnchantment extends Enchantment {

    private final int levels;
    private final int minXP;
    private final String registryName;

    protected SkateboardEnchantment(String name, Rarity r, int levels, int minXP) {
        super(r, IWSEnchantmentRegistry.SKATEBOARD, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        this.levels = levels;
        this.minXP = minXP;
        this.registryName = name;
    }

    public int getMinCost(int i) {
        return minXP + (i - 1) * 10;
    }

    public int getMaxCost(int i) {
        return super.getMinCost(i) + 30;
    }

    public int getMaxLevel() {
        return levels;
    }


    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment && IWSEnchantmentRegistry.areCompatible(this, enchantment);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack);
    }

    public String getName() {
        return registryName;
    }

    public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        return this.registryName.equals("bashing") && enchantedItem.is(IWSItemRegistry.SKATEBOARD.get()) ? 2.0F * level : 0.0F;
    }
}
