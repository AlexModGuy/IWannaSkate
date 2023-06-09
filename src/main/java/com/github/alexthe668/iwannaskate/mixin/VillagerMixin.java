package com.github.alexthe668.iwannaskate.mixin;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.HasAnimationFlags;
import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.misc.IWSAdvancements;
import com.github.alexthe668.iwannaskate.server.potion.IWSEffectRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements HasAnimationFlags {

    private static final EntityDataAccessor<Integer> IWS_ANIMATION_FLAGS = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.INT);

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    public abstract void setVillagerData(VillagerData p_35437_);

    private int useEnergyDrinkTime = 0;
    private int skateNitwitTime = 0;

    public VillagerMixin(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }


    @Inject(
            at = {@At("TAIL")},
            remap = true,
            method = {"Lnet/minecraft/world/entity/npc/Villager;defineSynchedData()V"}
    )
    private void iws_registerData(CallbackInfo ci) {
        this.entityData.define(IWS_ANIMATION_FLAGS, 0);
    }


    @Inject(
            method = {"Lnet/minecraft/world/entity/npc/Villager;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_pickUpItem(ItemEntity item, CallbackInfo ci) {
        ItemStack stack = item.getItem();
        if (stack.is(IWSItemRegistry.ENERGY_DRINK.get()) || stack.is(IWSItemRegistry.SKATEBOARD.get())) {
            if (this.getMainHandItem().isEmpty()) {
                Entity itemThrower = item.getOwner();
                if(itemThrower instanceof Player player && !level().isClientSide){
                    IWSAdvancements.trigger(player, IWSAdvancements.GIVE_VILLAGER_DRINK);
                }
                ItemStack copy = stack.copy();
                copy.setCount(1);
                this.setItemInHand(InteractionHand.MAIN_HAND, copy);
                stack.shrink(1);
            }
            ci.cancel();
        }
    }


    @Inject(
            method = {"Lnet/minecraft/world/entity/npc/Villager;wantsToPickUp(Lnet/minecraft/world/item/ItemStack;)Z"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_wantsToPickup(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.isBaby() && stack.is(IWSItemRegistry.ENERGY_DRINK.get()) && this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && IWannaSkateMod.COMMON_CONFIG.convertVillagersToNitwits.get()) {
            cir.setReturnValue(true);
        } else if (!this.isBaby() && this.getVillagerData().getProfession() == VillagerProfession.NITWIT && IWannaSkateMod.COMMON_CONFIG.convertNitwitsToSkaters.get()) {
            if (stack.is(IWSItemRegistry.SKATEBOARD.get())) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/npc/Villager;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void iws_tick(CallbackInfo ci) {
        ItemStack stack = this.getMainHandItem();
        int currentAnimFlag = 0;
        if (stack != null) {
            if (this.isBaby() && stack.is(IWSItemRegistry.ENERGY_DRINK.get()) && IWannaSkateMod.COMMON_CONFIG.convertVillagersToNitwits.get()) {
                if (useEnergyDrinkTime < 60) {
                    if (useEnergyDrinkTime % 5 == 0) {
                        this.playSound(stack.getDrinkingSound(), 0.5F, this.level().random.nextFloat() * 0.1F + 0.9F);
                    }
                    currentAnimFlag = 1;
                    useEnergyDrinkTime++;
                } else {
                    useEnergyDrinkTime = 0;
                    stack.shrink(1);
                    if (random.nextBoolean()) {
                        this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NITWIT));
                        this.addEffect(new MobEffectInstance(IWSEffectRegistry.OVERCAFFEINATED.get(), 1200));
                    }
                }
            }
            if (!this.isBaby() && this.getVillagerData().getProfession() == VillagerProfession.NITWIT && IWannaSkateMod.COMMON_CONFIG.convertNitwitsToSkaters.get()) {
                if (stack.is(IWSItemRegistry.SKATEBOARD.get())) {
                    SkateboardEntity spawnedBoard = IWSEntityRegistry.SKATEBOARD.get().create(level());
                    spawnedBoard.setItemStack(stack.copy());
                    spawnedBoard.setPos(this.position());
                    spawnedBoard.setYRot(this.getYRot());
                    spawnedBoard.setMobSpawned();
                    spawnedBoard.setXRot(-70);
                    if (level().addFreshEntity(spawnedBoard)) {
                        this.swing(InteractionHand.OFF_HAND, true);
                        stack.shrink(1);
                    }
                    this.startRiding(spawnedBoard);
                    this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
                }
                if (this.isPassenger() && this.getVehicle() instanceof SkateboardEntity) {
                    skateNitwitTime++;
                }
                if (skateNitwitTime > 300 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, IWSEntityRegistry.WANDERING_SKATER.get(), (timer) -> this.skateNitwitTime = timer)) {
                    this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);
                    WanderingSkaterEntity wanderingSkaterEntity = this.convertTo(IWSEntityRegistry.WANDERING_SKATER.get(), true);
                    wanderingSkaterEntity.setNoDespawn(true);
                    if (wanderingSkaterEntity != null) {
                        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, wanderingSkaterEntity);
                    }
                }
            }
        }
        setIWSAnimationFlags(currentAnimFlag);
    }

    public int getIWSAnimationFlags() {
        return this.entityData.get(IWS_ANIMATION_FLAGS);
    }

    public void setIWSAnimationFlags(int flags) {
        this.entityData.set(IWS_ANIMATION_FLAGS, flags);
    }

}
