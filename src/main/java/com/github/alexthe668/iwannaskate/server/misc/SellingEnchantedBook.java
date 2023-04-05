package com.github.alexthe668.iwannaskate.server.misc;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;

public class SellingEnchantedBook implements VillagerTrades.ItemListing {
    private final Enchantment enchantment;
    private final int price;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public SellingEnchantedBook(Enchantment enchantment, int price, int maxUses, int xpValue) {
        this.enchantment = enchantment;
        this.price = price;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = 0.05F;
    }

    public MerchantOffer getOffer(Entity tradingWith, RandomSource randomSource) {
        int i = Mth.nextInt(randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel());
        ItemStack itemstack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i));
        int j = 2 + randomSource.nextInt(5 + i * 10) + 3 * i;
        if (enchantment.isTreasureOnly()) {
            j *= 2;
        }

        if (j > 64) {
            j = 64;
        }
        return new MerchantOffer(new ItemStack(Items.EMERALD, this.price), itemstack, this.maxUses, this.xpValue, this.priceMultiplier);
    }
}