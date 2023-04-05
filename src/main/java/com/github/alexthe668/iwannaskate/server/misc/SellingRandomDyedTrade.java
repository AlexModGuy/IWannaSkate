package com.github.alexthe668.iwannaskate.server.misc;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public class SellingRandomDyedTrade implements VillagerTrades.ItemListing {
    private final ItemStack tradeItem;
    private final int price;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public SellingRandomDyedTrade(ItemStack itemLike, int price, int maxUses, int xpValue) {
        this.tradeItem = itemLike;
        this.price = price;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = 0.05F;
    }

    public MerchantOffer getOffer(Entity tradingWith, RandomSource randomSource) {
        ItemStack selling = tradeItem.copy();
        if(selling.getItem() instanceof DyeableLeatherItem dyeableLeatherItem){
            dyeableLeatherItem.setColor(selling, (int) (randomSource.nextFloat() * 0xFFFFFF));
        }
        return new MerchantOffer(new ItemStack(Items.EMERALD, this.price), selling, this.maxUses, this.xpValue, this.priceMultiplier);
    }
}