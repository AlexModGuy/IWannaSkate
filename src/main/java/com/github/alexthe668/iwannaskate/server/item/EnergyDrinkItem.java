package com.github.alexthe668.iwannaskate.server.item;

import com.github.alexthe668.iwannaskate.server.potion.IWSEffectRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class EnergyDrinkItem extends Item {

    public EnergyDrinkItem(Item.Properties properties) {
        super(properties);
    }

    public UseAnim getUseAnimation(ItemStack p_41358_) {
        return UseAnim.DRINK;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }
        int effectLevel = livingEntity.hasEffect(IWSEffectRegistry.HIGH_OCTANE.get()) ? 1 + livingEntity.getEffect(IWSEffectRegistry.HIGH_OCTANE.get()).getAmplifier() : 0;
        if (!level.isClientSide) {
            int duration = 600;
            if(effectLevel >= 2){
                livingEntity.addEffect(new MobEffectInstance(IWSEffectRegistry.OVERCAFFEINATED.get(), 180 * 20, 0));
            }else{
                livingEntity.addEffect(new MobEffectInstance(IWSEffectRegistry.HIGH_OCTANE.get(), duration * 20, effectLevel, false, false, true));
            }
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }

    public int getUseDuration(ItemStack stack) {
        return 20;
    }
}
