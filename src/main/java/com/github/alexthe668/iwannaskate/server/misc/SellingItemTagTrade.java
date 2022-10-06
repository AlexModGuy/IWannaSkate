package com.github.alexthe668.iwannaskate.server.misc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

public class SellingItemTagTrade implements VillagerTrades.ItemListing {
    private final TagKey<Item> tradeItem;
    private final int price;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public SellingItemTagTrade(TagKey<Item> itemLike, int price, int maxUses, int xpValue) {
        this.tradeItem = itemLike;
        this.price = price;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = 0.05F;
    }

    public MerchantOffer getOffer(Entity tradingWith, RandomSource randomSource) {
        List<Item> items = ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(tradeItem)).collect(ImmutableList.toImmutableList());
        Item item;
        if(items.size() > 1){
            item = items.get(randomSource.nextInt(items.size() - 1));
        }else{
            item = items.get(0);
        }
        return new MerchantOffer(new ItemStack(item), new ItemStack(Items.EMERALD, this.price), this.maxUses, this.xpValue, this.priceMultiplier);
    }
}