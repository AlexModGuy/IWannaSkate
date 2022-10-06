package com.github.alexthe668.iwannaskate.server.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.github.alexthe668.iwannaskate.server.misc.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WanderingSkaterEntity extends WanderingTrader {

    private static final EntityDataAccessor<Optional<UUID>> SKATEBOARD_UUID = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SKATEBOARD_ID = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.INT);
    private int skateTimer = 0;
    private int attemptRecoveryTimer = 0;
    private int slowTimer = 0;

    public WanderingSkaterEntity(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKATEBOARD_UUID, Optional.empty());
        this.entityData.define(SKATEBOARD_ID, -1);
    }

    public static ItemStack createSkateboard() {
        ItemStack itemStack = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(Items.OAK_SLAB));
        data.setGripTape(DyeColor.GRAY);
        CompoundTag bannerTag = new CompoundTag();
        ListTag patterns = new ListTag();
        //TODO
        bannerTag.put("Patterns", patterns);
        bannerTag.putInt("Base", DyeColor.LIGHT_BLUE.getId());
        data.setBanner(bannerTag);
        SkateboardData.setStackData(itemStack, data);
        itemStack.setHoverName(Component.translatable("item.iwannaskate.skateboard.wandering_skater").withStyle(ChatFormatting.DARK_AQUA));
        return itemStack;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("SkateboardUUID")) {
            this.setSkateboardUUID(compound.getUUID("SkateboardUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getSkateboardUUID() != null) {
            compound.putUUID("SkateboardUUID", this.getSkateboardUUID());
        }
    }

    @Nullable
    public UUID getSkateboardUUID() {
        return this.entityData.get(SKATEBOARD_UUID).orElse(null);
    }

    public void setSkateboardUUID(@Nullable UUID uniqueId) {
        this.entityData.set(SKATEBOARD_UUID, Optional.ofNullable(uniqueId));
    }

    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(IWSItemRegistry.SKATEBOARD.get()) && this.getOffhandItem().isEmpty();
    }

    public boolean canPickUpLoot() {
        return true;
    }

    protected void pickUpItem(ItemEntity entity) {
        ItemStack itemstack = entity.getItem();
        if(this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()){
            this.setItemSlot(EquipmentSlot.OFFHAND, itemstack);
            this.take(entity, itemstack.getCount());
        }
        this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
    }

    public Entity getSkateboard() {
        if (!level.isClientSide) {
            UUID id = getSkateboardUUID();
            return id == null ? null : ((ServerLevel) level).getEntity(id);
        } else {
            int id = this.entityData.get(SKATEBOARD_ID);
            return id == -1 ? null : level.getEntity(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        Entity skateboard = getSkateboard();
        if (!level.isClientSide) {
            if (skateboard != null) {
                this.entityData.set(SKATEBOARD_ID, skateboard.getId());
            } else if ((skateTimer <= -300 || this.getTarget() != null) && this.canPlaceBoard()) {
                skateTimer = 0;
                SkateboardEntity spawnedBoard = IWSEntityRegistry.SKATEBOARD.get().create(level);
                ItemStack stack = this.getItemBySlot(EquipmentSlot.OFFHAND);
                spawnedBoard.setItemStack(stack.copy());
                spawnedBoard.setPos(this.position());
                spawnedBoard.setYRot(this.getYRot());
                spawnedBoard.setMobSpawned();
                spawnedBoard.setXRot(-70);
                if (level.addFreshEntity(spawnedBoard)) {
                    this.swing(InteractionHand.OFF_HAND, true);
                    stack.shrink(1);
                    this.setSkateboardUUID(spawnedBoard.getUUID());
                }
            }
            if (this.isPassenger() && this.getVehicle() instanceof SkateboardEntity rideBoard) {
                skateTimer++;
                if (rideBoard.getSlowdown() < 0.4F || rideBoard.getDeltaMovement().length() < 0.05 && this.getNavigation().isInProgress()) {
                    slowTimer++;
                }
                if (shouldDismountBoard(rideBoard)) {
                    this.setItemSlot(EquipmentSlot.OFFHAND, rideBoard.getItemStack().copy());
                    this.swing(InteractionHand.OFF_HAND, true);
                    rideBoard.remove(RemovalReason.DISCARDED);
                    this.stopRiding();
                    slowTimer = 0;
                }
            } else {
                if (skateTimer > 0) {
                    skateTimer = 0;
                }
                skateTimer--;
            }
        }
        if (!this.isPassenger() && skateboard != null) {
            if(this.isAlive() && attemptRecoveryTimer < 60){
                if (this.distanceToSqr(skateboard) > 1F) {
                    if (!level.isClientSide) {
                        this.getNavigation().moveTo(skateboard.getX(), skateboard.getY(0.5F), skateboard.getZ(), 1F);
                    }
                } else {
                    this.startRiding(skateboard);
                }
            }else if(skateboard instanceof  SkateboardEntity){
                this.setItemSlot(EquipmentSlot.OFFHAND, ((SkateboardEntity)skateboard).getItemStack().copy());
                this.swing(InteractionHand.OFF_HAND, true);
                skateboard.remove(RemovalReason.DISCARDED);
                attemptRecoveryTimer = 0;
            }
            attemptRecoveryTimer++;
        }
    }

    private boolean canPlaceBoard() {
        return !this.isTrading() && this.isOnGround() && this.getItemBySlot(EquipmentSlot.OFFHAND).is(IWSItemRegistry.SKATEBOARD.get()) && SkateQuality.getSkateQuality(this.getBlockStateOn(), SkateQuality.LOW) != SkateQuality.LOW && level.isUnobstructed(this);
    }

    private boolean shouldDismountBoard(SkateboardEntity board) {
        return slowTimer > 60 || this.isTrading() || board.isRemoveLogic() || this.getTarget() == null && skateTimer > 700 || !this.isAlive();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType mobType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        spawnGroupData = super.finalizeSpawn(level, difficulty, mobType, spawnGroupData, tag);
        RandomSource randomsource = level.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficulty);
        this.populateDefaultEquipmentEnchantments(randomsource, difficulty);
        return spawnGroupData;
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        this.setItemSlot(EquipmentSlot.OFFHAND, createSkateboard());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource == DamageSource.IN_WALL || damageSource == IWSDamageTypes.SKATE_DAMAGE;
    }

    protected void updateTrades() {
        MerchantOffers merchantoffers = this.getOffers();
        VillagerTrades.ItemListing[] trades = new VillagerTrades.ItemListing[]{
                new BuyingItemTrade(new ItemStack(SkateboardWheels.DEFAULT.getItemRegistryObject().get(), 2), 1, 7, 3),
                new SellingItemTrade(new ItemStack(IWSItemRegistry.SKATEBOARD_TRUCK.get(), 2), 3, 2, 4),
                new SellingItemTrade(new ItemStack(Items.IRON_NUGGET, 18), 4, 3, 5),
                new SellingItemTrade(new ItemStack(Items.IRON_HELMET, 1), 7, 2, 5),
                new SellingRandomSkateboardTrade(new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get()), 2, 4, 3),
                new SellingRandomSkateboardTrade(new ItemStack(IWSItemRegistry.SKATEBOARD.get()), 11, 1, 3),
                new SellingEnchantedBook(IWSEnchantmentRegistry.SLOW_MOTION.get(), 30, 2, 3),
                new SellingEnchantedBook(IWSEnchantmentRegistry.SURFING.get(), 30, 2, 3)
        };
        this.addOffersFromItemListings(merchantoffers, trades, 4);
        int randomAttempts = 0;
        List<SkateboardWheels> wheelsList = new ArrayList<>();
        for(SkateboardWheels wheels : SkateboardWheels.values()){
            if(wheels.isTrade()){
                wheelsList.add(wheels);
            }
        }
        SkateboardWheels wheel1 = wheelsList.get(Mth.clamp(wheelsList.size() <= 1 ? 0 : this.random.nextInt(wheelsList.size()), 0, wheelsList.size() - 1));
        SkateboardWheels wheel2 = wheel1;
        while(wheel1 == wheel2 && randomAttempts < 255){
            randomAttempts++;
            wheel2 = wheelsList.get(Mth.clamp(wheelsList.size() <= 1 ? 0 : this.random.nextInt(wheelsList.size()), 0, wheelsList.size() - 1));
        }
        SellingItemTrade wheelTrade1 = new SellingItemTrade(new ItemStack(wheel1.getItemRegistryObject().get(), 2), 4, 4, 4);
        SellingItemTrade wheelTrade2 = new SellingItemTrade(new ItemStack(wheel2.getItemRegistryObject().get(), 2), 4, 4, 4);
        MerchantOffer merchantoffer1 = wheelTrade1.getOffer(this, this.random);
        MerchantOffer merchantoffer2 = wheelTrade2.getOffer(this, this.random);
        if (merchantoffer1 != null) {
            merchantoffers.add(merchantoffer1);
        }
        if (merchantoffer2 != null) {
            merchantoffers.add(merchantoffer2);
        }
    }

}
