package com.github.alexthe668.iwannaskate.server.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.github.alexthe668.iwannaskate.server.misc.IWSDamageTypes;
import com.github.alexthe668.iwannaskate.server.misc.IWSSoundRegistry;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import com.github.alexthe668.iwannaskate.server.misc.SkateQuality;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class SkaterSkeletonEntity extends AbstractSkeleton {

    private static final EntityDataAccessor<Optional<UUID>> SKATEBOARD_UUID = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SKATEBOARD_ID = SynchedEntityData.defineId(SkaterSkeletonEntity.class, EntityDataSerializers.INT);
    private int skateTimer = 0;
    private int attemptRecoveryTimer = 0;
    private int slowTimer = 0;

    protected SkaterSkeletonEntity(EntityType type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.MAX_HEALTH, 28.0D);
    }

    public static ItemStack createSkateboard() {
        ItemStack itemStack = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(Items.DARK_OAK_SLAB));
        data.setGripTape(DyeColor.BLACK);
        CompoundTag bannerTag = new CompoundTag();
        ListTag patterns = new ListTag();
        CompoundTag layer1 = new CompoundTag();
        layer1.putString("Pattern", "sku");
        layer1.putInt("Color", 15);
        patterns.add(layer1);

        CompoundTag layer2 = new CompoundTag();
        layer2.putString("Pattern", "tts");
        layer2.putInt("Color", 15);
        patterns.add(layer2);

        CompoundTag layer3 = new CompoundTag();
        layer3.putString("Pattern", "bts");
        layer3.putInt("Color", 15);
        patterns.add(layer3);

        bannerTag.put("Patterns", patterns);
        bannerTag.putInt("Base", DyeColor.RED.getId());
        data.setBanner(bannerTag);
        SkateboardData.setStackData(itemStack, data);
        return itemStack;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKATEBOARD_UUID, Optional.empty());
        this.entityData.define(SKATEBOARD_ID, -1);
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
        return stack.is(IWSItemRegistry.SKATEBOARD.get()) && this.getMainHandItem().isEmpty();
    }

    public boolean canPickUpLoot() {
        return true;
    }

    protected void pickUpItem(ItemEntity entity) {
        super.pickUpItem(entity);
        this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
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

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.reassessWeaponGoal();
        return spawngroupdata;
    }

    public boolean isAggressive() {
        return false;
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
                ItemStack stack = this.getItemBySlot(EquipmentSlot.MAINHAND);
                spawnedBoard.setItemStack(stack.copy());
                spawnedBoard.setPos(this.position());
                spawnedBoard.setYRot(this.getYRot());
                spawnedBoard.setMobSpawned();
                spawnedBoard.setXRot(-70);
                if (level.addFreshEntity(spawnedBoard)) {
                    this.swing(InteractionHand.MAIN_HAND, true);
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
                    this.setItemSlot(EquipmentSlot.MAINHAND, rideBoard.getItemStack().copy());
                    this.swing(InteractionHand.MAIN_HAND, true);
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
                this.setItemSlot(EquipmentSlot.MAINHAND, ((SkateboardEntity)skateboard).getItemStack().copy());
                this.swing(InteractionHand.MAIN_HAND, true);
                skateboard.remove(RemovalReason.DISCARDED);
                attemptRecoveryTimer = 0;
            }
            attemptRecoveryTimer++;
        }
    }

    private boolean canPlaceBoard() {
        return IWannaSkateMod.COMMON_CONFIG.skaterSkeletonsUseSkateboards.get() && this.isOnGround() && this.getItemBySlot(EquipmentSlot.MAINHAND).is(IWSItemRegistry.SKATEBOARD.get()) && SkateQuality.getSkateQuality(this.getBlockStateOn(), SkateQuality.LOW) != SkateQuality.LOW && level.isUnobstructed(this);
    }

    private boolean shouldDismountBoard(SkateboardEntity board) {
        return slowTimer > 60 || board.isRemoveLogic() || this.getTarget() == null && skateTimer > 700 || !this.isAlive();
    }

    public static boolean checkSkaterSkeletonSpawnRules(EntityType type, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        BlockPos blockpos = pos.below();
        return spawnType == MobSpawnType.SPAWNER || IWannaSkateMod.COMMON_CONFIG.spawnSkaterSkeletons.get() && !levelAccessor.getBiome(blockpos).is(IWSTags.NO_MONSTERS) && levelAccessor.getBlockState(blockpos).is(IWSTags.SPAWNS_SKATER_SKELETONS) && levelAccessor.getBlockState(blockpos).isValidSpawn(levelAccessor, blockpos, type);
    }

    public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType type) {
        return IWannaSkateMod.COMMON_CONFIG.spawnSkaterSkeletons.get();
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        this.setItemSlot(EquipmentSlot.MAINHAND, createSkateboard());
    }

    protected SoundEvent getAmbientSound() {
        return IWSSoundRegistry.SKATER_SKELETON_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33579_) {
        return IWSSoundRegistry.SKATER_SKELETON_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return IWSSoundRegistry.SKATER_SKELETON_DIE.get();
    }

    protected SoundEvent getStepSound() {
        return IWSSoundRegistry.SKATER_SKELETON_WALK.get();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource == DamageSource.IN_WALL || damageSource == IWSDamageTypes.SKATE_DAMAGE;
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
        Entity entity = source.getEntity();

        int i = net.minecraftforge.common.ForgeHooks.getLootingLevel(this, entity, source);
        this.captureDrops(new java.util.ArrayList<>());

        boolean flag = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropLoot() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable(source, flag);
            this.dropCustomDeathLoot(source, i, flag);
        }
        //music disc check
        this.dropEquipment();
        this.dropExperience();

        Collection<ItemEntity> drops = captureDrops(null);
        Collection<ItemEntity> processedDrops = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for(ItemEntity itemEntity : drops){
            if(itemEntity.getItem().is(IWSTags.SKATEBOARD_WHEELS) && this.getRandom().nextFloat() < IWannaSkateMod.COMMON_CONFIG.skaterSkeletonsHolidayWheelsDropChance.get()){
                int count = itemEntity.getItem().getCount();
                if(calendar.get(2) + 1 == 10){
                    itemEntity.setItem(new ItemStack(SkateboardWheels.SPOOKY.getItemRegistryObject().get(), count));
                }
                if(calendar.get(2) + 1 == 12){
                    itemEntity.setItem(new ItemStack(SkateboardWheels.SNOWY.getItemRegistryObject().get(), count));
                }
                processedDrops.add(itemEntity);
            }else{
                processedDrops.add(itemEntity);
            }
        }
        if (!net.minecraftforge.common.ForgeHooks.onLivingDrops(this, source, processedDrops, i, lastHurtByPlayerTime > 0))
            processedDrops.forEach(e -> level.addFreshEntity(e));
    }

    private void spawnAndLocalizeToHoliday(ItemEntity e) {

        level.addFreshEntity(e);
    }

}
