package com.github.alexthe668.iwannaskate.server.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.entity.ai.WanderingSkaterApproachPositionGoal;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.github.alexthe668.iwannaskate.server.misc.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WanderingSkaterEntity extends WanderingTrader {

    private static final EntityDataAccessor<Optional<UUID>> SKATEBOARD_UUID = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SKATEBOARD_ID = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.INT);
    private int skateTimer = 0;
    private int attemptRecoveryTimer = 0;
    private int slowTimer = 0;
    private float attackingProgress;
    private float prevAttackingProgress;
    private float attackAngle;
    private float prevAttackAngle;
    private int lastAttackTimestamp = 0;
    private int healCooldown = 0;
    @Nullable
    private BlockPos wanderingSkaterTarget;

    public WanderingSkaterEntity(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new UseItemGoal<>(this, new ItemStack(IWSItemRegistry.PIZZA_SLICE.get()), SoundEvents.PLAYER_BURP, (p_35882_) -> {
            return this.getHealth() < this.getMaxHealth() && this.healCooldown == 0 && !shouldBeAttacking();
        }));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 12.0F, 0.5D, 0.5D){
            public boolean canUse(){
                return WanderingSkaterEntity.this.getHealth() < WanderingSkaterEntity.this.getMaxHealth() * 0.5F && super.canUse();
            }

            public void start() {
                super.start();
                WanderingSkaterEntity.this.skateTimer = -300;
            }
        });
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 0.5D));
        this.goalSelector.addGoal(2, new WanderingSkaterApproachPositionGoal(this, 2.0D, 0.35D));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35D));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new DefensiveTargetGoal<>(this, Zombie.class));
        this.targetSelector.addGoal(1, new DefensiveTargetGoal<>(this, Evoker.class));
        this.targetSelector.addGoal(1, new DefensiveTargetGoal<>(this, Pillager.class));
        this.targetSelector.addGoal(1, new DefensiveTargetGoal<>(this, Vex.class));
        this.targetSelector.addGoal(1, new DefensiveTargetGoal<>(this, Illusioner.class));
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


    @Override
    public double getVisibilityPercent(@Nullable Entity targeter) {
        return super.getVisibilityPercent(targeter) * 0.35F;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKATEBOARD_UUID, Optional.empty());
        this.entityData.define(SKATEBOARD_ID, -1);
        this.entityData.define(ATTACK_TIME, 0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("SkateboardUUID")) {
            this.setSkateboardUUID(compound.getUUID("SkateboardUUID"));
        }
        if (compound.contains("WanderTarget")) {
            this.wanderingSkaterTarget = NbtUtils.readBlockPos(compound.getCompound("WanderTarget"));
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
        if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
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
        prevAttackingProgress = attackingProgress;
        prevAttackAngle = attackAngle;
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
            if (this.isAlive() && attemptRecoveryTimer < 5) {
                if (this.distanceToSqr(skateboard) > 1F) {
                    if (!level.isClientSide) {
                        this.getNavigation().moveTo(skateboard.getX(), skateboard.getY(0.5F), skateboard.getZ(), 1F);
                    }
                } else {
                    this.startRiding(skateboard);
                }
            } else if (skateboard instanceof SkateboardEntity) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ((SkateboardEntity) skateboard).getItemStack().copy());
                this.swing(InteractionHand.OFF_HAND, true);
                skateboard.remove(RemovalReason.DISCARDED);
                attemptRecoveryTimer = 0;
            }
            attemptRecoveryTimer++;
        }
        int attackTime = this.getAttackTime();
        float angleIncrement = 45;
        if (attackTime > 0 && this.isAlive()) {
            if (this.attackingProgress < 5) {
                this.attackingProgress += 1.0F;
            } else {
                this.attackAngle += angleIncrement;
                if(this.getOffhandItem().is(IWSItemRegistry.SKATEBOARD.get())){
                    hurtMobsAtYaw(this.getYRot() + attackAngle);
                }
            }
            this.navigation.stop();
            this.setAttackTime(attackTime - 1);
        } else {
            if (this.attackingProgress > 0) {
                this.attackingProgress -= 1.0F;
            }
            if (attackAngle > 0) {
                attackAngle = attackAngle % 360 - angleIncrement;
            }
        }
        LivingEntity target = this.getTarget();
        if(!level.isClientSide && this.getOffhandItem().is(IWSItemRegistry.SKATEBOARD.get()) && target != null && target.isAlive() && this.distanceToSqr(target) < 20F && this.hasLineOfSight(target) && this.getAttackTime() == 0){
            this.setAttackTime(30 + this.getRandom().nextInt(20));
            lastAttackTimestamp = tickCount;
        }
        if(healCooldown > 0){
            healCooldown--;
        }
    }

    private void hurtMobsAtYaw(float hurtYaw) {
        Vec3 offset = new Vec3(0, -0.3F, 1.15F).yRot(-hurtYaw * ((float) Math.PI / 180F));
        Vec3 at = this.getEyePosition().add(offset);
        AABB mobBox = new AABB(at.x - 1, at.y - 1.5F, at.z - 1, at.x + 1, at.y + 1.5, at.z + 1);
        for (Entity entity : this.level.getEntities(this, mobBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (entity instanceof LivingEntity living && canHurtWithAttack(living)) {
                if (living.hurt(DamageSource.mobAttack(this), 4)) {

                    living.knockback(1D, (double)Mth.sin(hurtYaw * ((float)Math.PI / 180F)), (double)(-Mth.cos(hurtYaw * ((float)Math.PI / 180F))));
                }
            }
        }
    }

    private boolean canHurtWithAttack(LivingEntity living) {
        LivingEntity target = this.getTarget();
        return living != this && !living.isAlliedTo(this) && this.hasLineOfSight(living) && (living instanceof Enemy || target != null && target.is(living)) && !(living instanceof Creeper);
    }

    private boolean canPlaceBoard() {
        return !this.isTrading() && this.isAlive() && this.isOnGround() && !this.shouldBeAttacking() && this.getItemBySlot(EquipmentSlot.OFFHAND).is(IWSItemRegistry.SKATEBOARD.get()) && SkateQuality.getSkateQuality(this.getBlockStateOn(), SkateQuality.LOW) != SkateQuality.LOW && level.isUnobstructed(this);
    }

    private boolean shouldDismountBoard(SkateboardEntity board) {
        return slowTimer > 60 || this.shouldBeAttacking() || this.isTrading() || board.isRemoveLogic() || this.getTarget() == null && skateTimer > 700 || !this.isAlive();
    }

    private boolean shouldBeAttacking() {
        LivingEntity target = this.getTarget();
        if(target != null){
            return this.distanceToSqr(target) > 16 || Math.abs(tickCount - lastAttackTimestamp) < 60;
        }
        return this.getAttackTime() > 0;
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

    @Override
    protected void completeUsingItem() {
        if(this.getMainHandItem().is(IWSItemRegistry.PIZZA_SLICE.get())){
            this.healCooldown = 100;
            this.heal(3);
        }
        super.completeUsingItem();
    }
    private int getAttackTime() {
        return this.entityData.get(ATTACK_TIME);
    }

    private void setAttackTime(int time) {
        this.entityData.set(ATTACK_TIME, time);
    }

    public float getAttackingProgress(float partialTick) {
        return (prevAttackingProgress + (attackingProgress - prevAttackingProgress) * partialTick) * 0.2F;
    }

    public float getAttackingAngle(float partialTick) {
        return prevAttackAngle + (attackAngle - prevAttackAngle) * partialTick;
    }

    protected void updateTrades() {
        MerchantOffers merchantoffers = this.getOffers();
        VillagerTrades.ItemListing[] trades = new VillagerTrades.ItemListing[]{
                new BuyingItemTrade(new ItemStack(SkateboardWheels.DEFAULT.getItemRegistryObject().get(), 2), 1, 7, 3),
                new SellingItemTrade(new ItemStack(IWSItemRegistry.SKATEBOARD_TRUCK.get(), 2), 3, 2, 4),
                new SellingItemTrade(new ItemStack(Items.IRON_NUGGET, 18), 4, 3, 5),
                new SellingItemTrade(new ItemStack(Items.IRON_HELMET, 1), 7, 2, 5),
                new SellingItemTrade(new ItemStack(IWSItemRegistry.PIZZA_SLICE.get(), 5), 2, 5, 3),
                new SellingRandomSkateboardTrade(new ItemStack(IWSItemRegistry.SKATEBOARD_DECK.get()), 2, 4, 3),
                new SellingRandomSkateboardTrade(new ItemStack(IWSItemRegistry.SKATEBOARD.get()), 11, 1, 3),
                new SellingEnchantedBook(IWSEnchantmentRegistry.SLOW_MOTION.get(), 30, 2, 3),
                new SellingEnchantedBook(IWSEnchantmentRegistry.SURFING.get(), 30, 2, 3),
                new SellingItemTrade(new ItemStack(IWSItemRegistry.SHIMMERING_WAX.get(), 1), 3, 4, 3),
        };
        this.addOffersFromItemListings(merchantoffers, trades, 4);
        int randomAttempts = 0;
        List<SkateboardWheels> wheelsList = new ArrayList<>();
        for (SkateboardWheels wheels : SkateboardWheels.values()) {
            if (wheels.isTrade()) {
                wheelsList.add(wheels);
            }
        }
        SkateboardWheels wheel1 = wheelsList.get(Mth.clamp(wheelsList.size() <= 1 ? 0 : this.random.nextInt(wheelsList.size()), 0, wheelsList.size() - 1));
        SkateboardWheels wheel2 = wheel1;
        while (wheel1 == wheel2 && randomAttempts < 255) {
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

    @Override
    public void setWanderTarget(@Nullable BlockPos pos) {
        this.wanderingSkaterTarget = pos;
        super.setWanderTarget(pos);
    }

    @Nullable
    public BlockPos getWanderingSkaterTarget() {
        return this.wanderingSkaterTarget;
    }

    class DefensiveTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public DefensiveTargetGoal(WanderingSkaterEntity skaterEntity, Class<T> targetClass) {
            super(skaterEntity, targetClass, true);
        }

        @Override
        public boolean canUse() {
            return WanderingSkaterEntity.this.getAttackTime() == 0 && super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double dist) {
            return this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D);
        }
    }
}
