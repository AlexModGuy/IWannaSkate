package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardMaterials;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public class SellingRandomSkateboardTrade implements VillagerTrades.ItemListing {
    private final ItemStack tradeItem;
    private final int price;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public SellingRandomSkateboardTrade(ItemStack itemLike, int price, int maxUses, int xpValue) {
        this.tradeItem = itemLike;
        this.price = price;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = 0.05F;
    }

    public MerchantOffer getOffer(Entity tradingWith, RandomSource randomSource) {
        ItemStack dataCopy = tradeItem.copy();
        SkateboardData data = SkateboardMaterials.generateRandomData(SkateboardMaterials.getSkateboardMaterials(), randomSource, tradeItem.getItem() == IWSItemRegistry.SKATEBOARD_DECK.get());
        SkateboardData.setStackData(dataCopy, data);
        return new MerchantOffer(new ItemStack(Items.EMERALD, this.price), dataCopy, this.maxUses, this.xpValue, this.priceMultiplier);
    }
}