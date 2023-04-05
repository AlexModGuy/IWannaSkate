package com.github.alexthe668.iwannaskate.server.entity;

import com.github.alexthe666.citadel.server.entity.IModifiesTime;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.tick.modifier.LocalEntityTickRateModifier;
import com.github.alexthe666.citadel.server.tick.modifier.TickRateModifier;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.particle.IWSParticleRegistry;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.entity.ai.SkaterMoveControl;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.github.alexthe668.iwannaskate.server.misc.*;
import com.github.alexthe668.iwannaskate.server.network.SkateboardKeyMessage;
import com.github.alexthe668.iwannaskate.server.potion.IWSEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkateboardEntity extends Entity implements PlayerRideableJumping, IModifiesTime {

    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Float> X_ROT = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> Z_ROT = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WHEEL_ROT = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> REMOVE_SOON = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STOP_MOVEMENT_FLAG = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> PEDAL_AMOUNT = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FORWARDS = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SKATER_POSE = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> GRINDING = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_MOB_SPAWNED = SynchedEntityData.defineId(SkateboardEntity.class, EntityDataSerializers.BOOLEAN);
    public final SkateboardPartEntity front;
    public final SkateboardPartEntity back;
    public final double[][] trailPositions = new double[64][4];
    public final Vec3[] lightningPositions = new Vec3[4];
    private final PartEntity<?>[] allParts;
    public SkateQuality skateQuality = SkateQuality.LOW;
    public boolean trickFlag = false;
    public int trailPosPointer = -1;
    private SkateboardData skateboardData = SkateboardData.DEFAULT;
    private Map<Enchantment, Integer> enchantments;
    private float prevZRot;
    private float prevWheelRot;
    private float onGroundProgress;
    private float prevOnGroundProgress;
    private SkaterPose prevSkaterPose = SkaterPose.NONE;
    private float prevSkaterPoseProgress = 0;
    private float skaterPoseProgress = 0;
    private float prevPedalAmount = 0;
    private int removeIn = 0;
    private int jiggleXTime;
    private float frontHeight;
    private float backHeight;
    private int offGroundTime = 0;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private int sprintDown = 0;
    private int jumpFor = 0;
    private Vec3 prevDelta = Vec3.ZERO;
    private BlockPos lastBlockPos = null;
    private double totalDistanceTraveled = 0;
    private double lastDamagedDistance = 0;
    private int soundTimer = 0;
    private final List<Entity> slowedDownEntities = new ArrayList<>();
    private int slowMotionCooldown = 100;
    private Player returnToPlayer = null;
    public EntityDimensions size;

    public SkateboardEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        front = new SkateboardPartEntity(this);
        back = new SkateboardPartEntity(this);
        this.allParts = new SkateboardPartEntity[]{front, back};
        this.onGroundProgress = 5.0F;
        this.prevOnGroundProgress = 5.0F;
        size = this.getType().getDimensions();
    }

    public SkateboardEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(IWSEntityRegistry.SKATEBOARD.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ITEMSTACK, ItemStack.EMPTY);
        this.entityData.define(X_ROT, 0.0F);
        this.entityData.define(Y_ROT, 0.0F);
        this.entityData.define(Z_ROT, 0.0F);
        this.entityData.define(WHEEL_ROT, 0.0F);
        this.entityData.define(FORWARDS, 0.0F);
        this.entityData.define(PEDAL_AMOUNT, 0.0F);
        this.entityData.define(REMOVE_SOON, false);
        this.entityData.define(STOP_MOVEMENT_FLAG, false);
        this.entityData.define(SKATER_POSE, 0);
        this.entityData.define(GRINDING, false);
        this.entityData.define(IS_MOB_SPAWNED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BoardStack")) {
            this.setItemStack(ItemStack.of(tag.getCompound("BoardStack")));
        }
        totalDistanceTraveled = tag.getFloat("TotalDistanceTraveled");
        lastDamagedDistance = tag.getFloat("LastDamagedDist");
        if (tag.getBoolean("SpawnedByMob")) {
            this.setMobSpawned();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (!this.getItemStack().isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.getItemStack().save(stackTag);
            tag.put("BoardStack", stackTag);
        }
        tag.putFloat("TotalDistanceTraveled", (float) totalDistanceTraveled);
        tag.putFloat("LastDamagedDist", (float) lastDamagedDistance);
        tag.putBoolean("SpawnedByMob", this.isMobSpawned());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
        this.skateboardData = SkateboardData.fromStack(item);
    }


    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (ITEMSTACK.equals(entityDataAccessor)) {
            this.skateboardData = SkateboardData.fromStack(getItemStack());
            this.enchantments = EnchantmentHelper.getEnchantments(getItemStack());
            refreshDimensions();
            front.refreshDimensions();
            back.refreshDimensions();
        }
        if (REMOVE_SOON.equals(entityDataAccessor)) {
            this.removeIn = 5;
        }
        if (SKATER_POSE.equals(entityDataAccessor)) {
            this.prevSkaterPoseProgress = 0.0F;
            this.skaterPoseProgress = 0.0F;

        }
    }

    public SkateboardData getSkateboardData() {
        return skateboardData == null ? SkateboardData.DEFAULT : skateboardData;
    }

    public float getXRot() {
        return this.entityData.get(X_ROT);
    }

    public void setXRot(float f) {
        this.entityData.set(X_ROT, f);
    }

    public float getYRot() {
        return this.entityData.get(Y_ROT);
    }

    public void setYRot(float f) {
        this.entityData.set(Y_ROT, f);
    }

    public float getZRot() {
        return this.entityData.get(Z_ROT);
    }

    public void setZRot(float f) {
        this.entityData.set(Z_ROT, f);
    }

    public float getZRot(float partialTick) {
        return prevZRot + (this.getZRot() - prevZRot) * partialTick;
    }

    public float getWheelRot() {
        return this.entityData.get(WHEEL_ROT);
    }

    public void setWheelRot(float f) {
        this.entityData.set(WHEEL_ROT, f);
    }

    public float getWheelRot(float partialTick) {
        return prevWheelRot + (this.getWheelRot() - prevWheelRot) * partialTick;
    }

    public float getOnGroundProgress(float partialTick) {
        return (prevOnGroundProgress + (this.onGroundProgress - prevOnGroundProgress) * partialTick) * 0.2F;
    }

    public float getPedalAmount() {
        return this.entityData.get(PEDAL_AMOUNT);
    }

    public void setPedalAmount(float f) {
        this.entityData.set(PEDAL_AMOUNT, f);
    }

    public float getPedalAmount(float partialTicks) {
        return prevPedalAmount + (this.getPedalAmount() - prevPedalAmount) * partialTicks;
    }

    public float getForwards() {
        return this.entityData.get(FORWARDS);
    }

    public void setForwards(float f) {
        this.entityData.set(FORWARDS, f);
    }

    public SkaterPose getSkaterPose() {
        return SkaterPose.get(this.entityData.get(SKATER_POSE));
    }

    public void setSkaterPose(SkaterPose animation) {
        this.entityData.set(SKATER_POSE, animation.ordinal());
    }

    public SkaterPose getPrevSkaterPose() {
        return prevSkaterPose;
    }

    public float getSkaterPoseProgress(float partialTick) {
        return (prevSkaterPoseProgress + (this.skaterPoseProgress - prevSkaterPoseProgress) * partialTick) * 0.2F;
    }

    public boolean isGrinding() {
        return this.entityData.get(GRINDING);
    }

    public void setGrinding(boolean b) {
        this.entityData.set(GRINDING, b);
    }

    public void setMobSpawned() {
        this.entityData.set(IS_MOB_SPAWNED, true);
    }

    public boolean isMobSpawned() {
        return this.entityData.get(IS_MOB_SPAWNED);
    }

    public Vec3 rotateVec(Vec3 vec3, boolean xRot, float partialTicks) {
        Vec3 rotated = vec3;
        if (xRot) {
            rotated = vec3.xRot(-this.getViewXRot(partialTicks) * ((float) Math.PI / 180F));
        }
        return rotated.yRot(-this.getViewYRot(partialTicks) * ((float) Math.PI / 180F));
    }

    public float approachRotation(float current, float target, float max) {
        float f = Mth.wrapDegrees(target - current);
        if (f > max) {
            f = max;
        }

        if (f < -max) {
            f = -max;
        }

        return Mth.wrapDegrees(current + f);
    }

    @Override
    public void tick() {

        super.tick();
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.prevOnGroundProgress = this.onGroundProgress;
        boolean grounded = this.isOnGround() && !this.isGrinding() && this.backHeight >= this.frontHeight;
        if (grounded && this.onGroundProgress < 5F) {
            this.onGroundProgress += 2.5F;
        }
        if (!grounded && this.onGroundProgress > 0F) {
            this.onGroundProgress -= 2.5F;
        }
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                //dont lerp y, doesnt change much
                double d6 = this.getY() + (this.ly - this.getY());
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(this.getYRot() + (float) (this.lyr - (double) this.getYRot()) / (float) this.lSteps);
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
                this.setRot(this.getYRot(), this.getXRot());
            } else {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
            }
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.prevZRot = this.getZRot();
            this.prevWheelRot = this.getWheelRot();
        } else {
            prevDelta = this.getDeltaMovement();
            this.checkInsideBlocks();
            if (this.hasEnchant(IWSEnchantmentRegistry.ONBOARDING.get())) {
                List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2F, 0.5F, 0.2F), EntitySelector.pushableBy(this));
                if (!list.isEmpty()) {
                    for (int j = 0; j < list.size(); ++j) {
                        Entity entity = list.get(j);
                        if (!entity.hasPassenger(this)) {
                            if (!this.isVehicle() && !entity.isPassenger() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player) && !entity.getType().is(IWSTags.CANNOT_SKATE)) {
                                entity.startRiding(this, true);
                            } else {
                                this.push(entity);
                            }
                        }
                    }
                }
            }
            this.tickMovement();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(getSlowdown(), getSlowdown(), getSlowdown()));
            this.totalDistanceTraveled += this.getDeltaMovement().horizontalDistance();
            if (this.totalDistanceTraveled - this.lastDamagedDistance >= 20) {
                this.lastDamagedDistance = this.totalDistanceTraveled;
                if (this.takesDistanceDurabilityDamage()) {
                    int setDamage = getItemStack().getDamageValue() + 1;
                    if (setDamage >= getItemStack().getMaxDamage() - 1) {
                        this.entityData.set(REMOVE_SOON, true);
                    } else {
                        this.getItemStack().setDamageValue(setDamage);
                    }
                }
            }
            if (this.totalDistanceTraveled % 100 < 1.0) {
                if (this.totalDistanceTraveled > 10000) {
                    IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.SKATE_10K);
                }
            }
            this.tickRotation();
        }
        this.tickMultipart();
        this.tickControls();
        this.tickSound();
        if (this.entityData.get(REMOVE_SOON)) {
            if (this.hasEnchant(IWSEnchantmentRegistry.INSTANT_RETURN.get()) && returnToPlayer != null) {
                if (!level.isClientSide && !returnToPlayer.addItem(this.getItemStack().copy())) {
                    spawnAtLocation(this.getItemStack().copy());
                }
                this.discard();
            } else {
                this.removeIn--;
                this.setZRot((float) Math.sin(this.removeIn * 0.3F * Math.PI) * 50F);
                if (this.removeIn <= 0 && !this.level.isClientSide) {
                    this.removeIn = 0;
                    spawnAtLocation(this.getItemStack().copy());
                    this.discard();
                }
            }

        }
        this.tickAnimation();
        if (this.lastBlockPos != this.blockPosition()) {
            skateQuality = SkateQuality.getSkateQuality(this.getBlockStateOn(), SkateQuality.LOW);
            this.lastBlockPos = this.blockPosition();
        }
        if (slowMotionCooldown > 0) {
            slowMotionCooldown--;
        }
        this.lastBlockPos = blockPosition();
    }

    private boolean takesDistanceDurabilityDamage() {
        return this.getFirstPassenger() != null && ((this.getFirstPassenger() instanceof Player player && !player.isCreative()) || !(this.getFirstPassenger() instanceof Player) && !this.getFirstPassenger().getType().is(IWSTags.MAINTAINS_SKATEBOARD_DURABILITY));
    }

    public double getSlowdown() {
        if (this.hasEnchant(IWSEnchantmentRegistry.SURFING.get()) && (this.isInWaterOrBubble() || this.isOnWater())) {
            return 0.95D;
        }
        double d = getBlockSlowdown();
        if (this.hasEnchant(IWSEnchantmentRegistry.EARTHCROSSER.get()) && d < 0.9) {
            d = 0.9D;
        }
        if (this.hasEnchant(IWSEnchantmentRegistry.BENTHIC.get()) && this.isInWaterOrBubble() && d < 0.9) {
            d = 0.9D;
        }
        return this.isInLava() ? 0.1F : this.isInWaterOrBubble() && !this.hasEnchant(IWSEnchantmentRegistry.BENTHIC.get()) ? 0.6F : this.isOnGround() ? d : 0.98F;
    }

    private double getBlockSlowdown() {
        return this.getOnGroundProgress(1.0F) >= 1.0F && skateQuality != null ? skateQuality.getInertia() : 0.95F;
    }

    private void tickControls() {
        if (sprintDown > 0) {
            sprintDown--;
        }
        if (this.level.isClientSide) {
            Player player = IWannaSkateMod.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this) && IWannaSkateMod.PROXY.isKeyDown(0)) {
                if (sprintDown < 2) {
                    IWannaSkateMod.sendMSGToServer(new SkateboardKeyMessage(this.getId(), player.getId(), 0));
                    sprintDown = 10;
                }
            }
        }
        if (this.jumpFor == 0 && this.isOnGround() && trickFlag) {
            this.playSound(IWSSoundRegistry.SKATEBOARD_JUMP_LAND.get(), 1, 0.8F + this.random.nextFloat() * 0.4F);
            trickFlag = false;
        }
    }

    private void tickAnimation() {
        this.prevSkaterPoseProgress = skaterPoseProgress;
        SkaterPose skaterPose = this.getSkaterPose();
        if (this.prevSkaterPose != skaterPose) {
            if (skaterPoseProgress < 5.0F) {
                skaterPoseProgress += 1F;
            } else if (skaterPoseProgress >= 5.0F) {
                this.prevSkaterPose = skaterPose;
            }
        } else {
            this.skaterPoseProgress = 5.0F;
        }
        float wheelRot = this.getWheelRot();
        float wheelSpeed = (float) Math.min(this.getDeltaMovement().horizontalDistance(), 1);
        if (wheelRot > 360F) {
            prevWheelRot = prevWheelRot - 360F;
            wheelRot = wheelRot - 360F;
        }
        this.setWheelRot(wheelRot + wheelSpeed * 100);
        this.prevWheelRot = wheelRot;
        if (level.isClientSide && this.skateboardData != null) {
            ParticleOptions particleType = this.skateboardData.getWheelType().getWheelParticles();
            if (particleType != null && (this.skateboardData.getWheelType().particleSpawnOverride(this) || this.random.nextFloat() < 0.5F)) {
                Vec3 center = this.position().add(0, 0.15 + getRenderOffGroundAmount(1.0F), 0);
                float ground = getOnGroundProgress(1.0F);
                float forwards = 0.8F * Math.abs((float) Math.sin(Math.toRadians(this.getXRot()))) * ground;
                float up = -0.45F * (float) Math.sin(Math.toRadians(this.getXRot())) * ground;
                float wheelSideways = this.skateboardData.getWheelType() == SkateboardWheels.HOVER ? 0.175F : 0.25F;
                Vec3 frontRightWheel = center.add(rotateVec(new Vec3(wheelSideways, up, forwards + 0.55F), true, 1.0F));
                Vec3 frontLeftWheel = center.add(rotateVec(new Vec3(-wheelSideways, up, forwards + 0.55F), true, 1.0F));
                Vec3 backRightWheel = center.add(rotateVec(new Vec3(wheelSideways, up, forwards - 0.55F), true, 1.0F));
                Vec3 backLeftWheel = center.add(rotateVec(new Vec3(-wheelSideways, up, forwards - 0.55F), true, 1.0F));
                Vec3 delta = this.getDeltaMovement().scale(1.0D);
                boolean deltaOverride = delta.length() > 0.04D;
                float particleChance = this.skateboardData.getWheelType().getParticleChancePerTick();
                if (deltaOverride || random.nextFloat() < particleChance) {
                    this.addWheelParticle(frontRightWheel, particleType);
                }
                if (deltaOverride || random.nextFloat() < particleChance) {
                    this.addWheelParticle(frontLeftWheel, particleType);
                }
                if (deltaOverride || random.nextFloat() < particleChance) {
                    this.addWheelParticle(backRightWheel, particleType);
                }
                if (deltaOverride || random.nextFloat() < particleChance) {
                    this.addWheelParticle(backLeftWheel, particleType);
                }
            }
        }
    }

    private void addWheelParticle(Vec3 wheelVec, ParticleOptions partice) {
        if (partice == ParticleTypes.PORTAL) {
            this.level.addParticle(partice, wheelVec.x, wheelVec.y, wheelVec.z, (this.random.nextDouble() - 0.5D), -this.random.nextDouble(), (this.random.nextDouble() - 0.5D));
        } else if (partice == IWSParticleRegistry.HALLOWEEN.get()) {
            this.level.addParticle(partice, wheelVec.x, wheelVec.y, wheelVec.z, (this.random.nextDouble() - 0.5D) * 0.35F, this.random.nextDouble() * 0.2F, (this.random.nextDouble() - 0.5D) * 0.35F);
        } else if (partice == ParticleTypes.SNOWFLAKE) {
            this.level.addParticle(partice, wheelVec.x, wheelVec.y, wheelVec.z, (this.random.nextDouble() - 0.5D) * 0.15F, this.random.nextDouble() * 0.1F, (this.random.nextDouble() - 0.5D) * 0.15F);
        } else if (partice == IWSParticleRegistry.BEE.get()) {
            Vec3 randomOffset = new Vec3((this.random.nextDouble() - 0.5D) * 1, (this.random.nextDouble()) * 0.75F, (this.random.nextDouble() - 0.5D) * 1);
            Vec3 spawning = wheelVec.add(randomOffset);
            this.level.addParticle(partice, spawning.x, spawning.y, spawning.z, wheelVec.x, wheelVec.y, wheelVec.z);
        } else if (partice == IWSParticleRegistry.HOVER.get()) {
            double speed = this.getDeltaMovement().horizontalDistance();
            double d = wheelVec.y;
            if (speed < 0.04F) {
                d += 0.1D;
            }
            this.level.addParticle(partice, wheelVec.x, d, wheelVec.z, this.getYRot(), -0.05F - 0.1F * getRenderOffGroundAmount(1.0F), speed);
        } else if (partice == IWSParticleRegistry.SPARKLE.get()) {
            this.level.addParticle(partice, wheelVec.x + (this.random.nextDouble() - 0.5D) * 0.2F - this.getDeltaMovement().x, wheelVec.y + (this.random.nextDouble() - 0.5D) * 0.2F - this.getDeltaMovement().y, wheelVec.z + (this.random.nextDouble() - 0.5D) * 0.2F - this.getDeltaMovement().z, 0, 0, 0);
        } else {
            Vec3 delta = this.getDeltaMovement();
            this.level.addParticle(partice, wheelVec.x, wheelVec.y, wheelVec.z, delta.x, delta.y, delta.z);
        }
    }

    private void tickMovement() {
        float heightDiff = this.backHeight - this.frontHeight;
        if (Math.abs(heightDiff) > 0) {
            this.setForwards(Mth.clamp(this.getForwards() - heightDiff * 0.8F, 0, getMaxForwardsTicks()));
        }
        this.setGrinding(this.isOnGround() && (this.getBlockStateOn().is(IWSTags.GRINDS) || this.getFeetBlockState().is(IWSTags.GRINDS)));
        boolean onWater = this.hasEnchant(IWSEnchantmentRegistry.SURFING.get()) && this.isOnWater();
        if (onWater) {
            this.setOnGround(true);
            if (this.tickCount % 50 == 0) {
                IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.SKATE_SURFING);
            }
        }
        float gravity = this.isInWaterOrBubble() ? -0.2F : -0.6F + this.getSlowMotionLevel() * 0.25F;
        if (this.hasEnchant(IWSEnchantmentRegistry.SURFING.get())) {
            if (onWater) {
                gravity = 0.0F;
            } else if (this.isInWaterOrBubble()) {
                gravity = 0.05F;
            }
        }
        if (isVehicle()) {
            this.hasImpulse = true;
            float forwardsOne = this.getForwards() / this.getMaxForwardsTicks();
            float forwardsStrength = (float) Math.pow(forwardsOne, 0.5F);
            float speed = Math.max(forwardsStrength, 0.01F * jumpFor) * getMasterSpeed();
            float yRot = this.getYRot();
            Vec3 prev = this.getDeltaMovement();
            if (this.isGrinding()) {
                Direction dir = this.getZRot() > 0 ? this.getMotionDirection().getClockWise() : this.getMotionDirection().getCounterClockWise();
                yRot = dir.toYRot();
                speed = 11.5F * getMasterSpeed();
                prev = Vec3.ZERO;
            }
            float f1 = -Mth.sin(yRot * ((float) Math.PI / 180F));
            float f2 = Mth.cos(yRot * ((float) Math.PI / 180F));
            float jumpAdd = jumpFor > 0 ? 0.5F + this.getSlowMotionLevel() * 0.2F : gravity;
            Vec3 moveVec = new Vec3(f1, 0, f2).scale(speed);
            Vec3 vec31 = prev.scale(0.975F).add(moveVec);
            this.setDeltaMovement(new Vec3(vec31.x, 0.975F * jumpAdd, vec31.z));
        } else {
            this.setDeltaMovement(new Vec3(0, gravity, 0));
            if (this.getSkaterPose() != SkaterPose.NONE) {
                this.setSkaterPose(SkaterPose.NONE);
            }
        }
        if (jumpFor > 0) {
            jumpFor--;
        }
    }

    private float getMasterSpeed() {
        float f = this.prevSkaterPose == null ? 0 : this.prevSkaterPose.getSpeed(this.getPedalAmount());
        float f1 = this.getSkaterPose().getSpeed(this.getPedalAmount());
        float poseProgress = this.getSkaterPoseProgress(1.0F);
        return f * (1 - poseProgress) + f1 * poseProgress;
    }

    public float getForwardsDecay() {
        float f = this.getSkaterPose() == SkaterPose.PEDAL ? 1F : this.getSkaterPose() == SkaterPose.CROUCH ? 0.15F : 0.25F;
        float inertial = 1F - (0.7F * this.getEnchantLevel(IWSEnchantmentRegistry.INERTIAL.get()) / 3F);
        return f * inertial;
    }

    public float getMaxForwardsTicks() {
        return 75F;
    }

    private void tickMultipart() {
        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        float ground = getOnGroundProgress(1.0F);
        float forwards = 0.25F * Math.abs((float) Math.sin(Math.toRadians(this.getXRot()))) * ground;
        float up = -0.25F * (float) Math.sin(Math.toRadians(this.getXRot())) * ground;
        Vec3 center = this.position();
        Vec3 frontPos = center.add(rotateVec(new Vec3(0, up, forwards + 0.55F), true, 1.0F));
        Vec3 backPos = center.add(rotateVec(new Vec3(0, up, forwards - 0.55F), true, 1.0F));
        this.front.setPos(frontPos);
        this.back.setPos(backPos);
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
        Vec3 sampleFrontHeight = center.add(rotateVec(new Vec3(0, 0, 0.55F), false, 1.0f));
        Vec3 sampleBackHeight = center.add(rotateVec(new Vec3(0, 0, -0.55F), false, 1.0f));
        frontHeight = this.front.getHeightBelow(sampleFrontHeight, frontHeight);
        backHeight = this.back.getHeightBelow(sampleBackHeight, backHeight);

        if (level.isClientSide) {
            if (this.getSkateboardData().getWheelType().hasTrail()) {
                if (this.trailPosPointer < 0) {
                    for (int i = 0; i < this.trailPositions.length; ++i) {
                        this.trailPositions[i][0] = this.getX();
                        this.trailPositions[i][1] = this.getY();
                        this.trailPositions[i][2] = this.getZ();
                        this.trailPositions[i][3] = this.getYRot();
                    }
                }
                if (++this.trailPosPointer == this.trailPositions.length) {
                    this.trailPosPointer = 0;
                }
                this.trailPositions[this.trailPosPointer][0] = this.getX();
                this.trailPositions[this.trailPosPointer][1] = this.getY();
                this.trailPositions[this.trailPosPointer][2] = this.getZ();
                this.trailPositions[this.trailPosPointer][3] = this.getYRot();
            }
            if (this.getSkateboardData().getWheelType() == SkateboardWheels.SHOCKING) {
                int i = Mth.clamp(random.nextInt(4), 0, 3);
                lightningPositions[i] = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            }
        }
    }

    private void tickRotation() {
        if (jiggleXTime > 0) {
            jiggleXTime--;
        }
        if (this.isOnGround()) {
            float forwards = 0.25F * Math.abs((float) Math.sin(Math.toRadians(this.getXRot()))) * getOnGroundProgress(1);
            Vec3 frontPos = rotateVec(new Vec3(0, 0, forwards + 0.55F), false, 1.0F).add(0, frontHeight, 0);
            Vec3 backPos = rotateVec(new Vec3(0, 0, forwards - 0.55F), false, 1.0F).add(0, backHeight, 0);
            Vec3 sub = frontPos.subtract(backPos);
            float groundXRotTarget = (float) ((Mth.atan2(sub.y, sub.horizontalDistance()) * (double) (180F / (float) Math.PI)));
            float baselineXRot = Mth.clamp(groundXRotTarget, -60, 60);
            float x1 = approachRotation(this.getXRot(), baselineXRot, 15);
            if (this.getSkaterPose() == SkaterPose.OLLIE) {
                x1 = approachRotation(this.getXRot(), -60, 40);
            } else if (this.isGrinding()) {
                x1 = approachRotation(this.getXRot(), -35, 15);
            }
            if (x1 == 0 && Math.abs(this.getXRot() - x1) > 5.0F) {
                if (jiggleXTime == 0) {
                    this.playSound(IWSSoundRegistry.SKATEBOARD_JUMP_START.get(), 1, 0.8F + this.random.nextFloat() * 0.4F);
                }
                jiggleXTime = 5;
            }
            if (jiggleXTime > 0) {
                x1 -= Math.abs((float) Math.sin((5 - jiggleXTime) * 0.25F * Math.PI) * 4);
            }
            this.setXRot(x1);
            if (!this.isRemoveLogic() && !this.isGrinding()) {
                if (this.isVehicle()) {
                    float forwardsMin = Math.min(1, this.getForwards());
                    float sidewinderAdd = this.getEnchantLevel(IWSEnchantmentRegistry.SIDEWINDER.get()) * 3;
                    float y1 = this.getZRot() * forwardsMin;
                    float max = 5 + sidewinderAdd;
                    this.setYRot(approachRotation(this.getYRot(), this.getYRot() + y1, max));
                }
                this.setZRot(approachRotation(this.getZRot(), 0, 5));
            }
            offGroundTime = 0;
        } else {
            offGroundTime++;
            float f2 = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
            this.setXRot(approachRotation(this.getXRot(), f2, 5));
        }
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return !this.isRemoveLogic();
    }

    public boolean isPushable() {
        return !this.isRemoveLogic();
    }

    public boolean isPickable() {
        return !this.isRemoveLogic();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoveLogic();
    }

    public boolean isAttackable() {
        return !this.isRemoveLogic();
    }

    public boolean isOnGround() {
        return super.isOnGround() || this.hasEnchant(IWSEnchantmentRegistry.SURFING.get()) && this.isOnWater();
    }

    private boolean isOnWater() {
        BlockPos ourPos = BlockPos.containing(this.getX(), this.getY() + 0.4F, this.getZ());
        BlockPos underPos = this.getOnPos();
        return level.getFluidState(underPos).is(FluidTags.WATER) && !level.getFluidState(ourPos).is(FluidTags.WATER);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else {
            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
    }

    public boolean isRemoveLogic() {
        return this.entityData.get(REMOVE_SOON) || this.isRemoved();
    }

    @Override
    public boolean isMultipartEntity() {
        return !isRemoveLogic();
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return allParts;
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        for (PartEntity part : this.allParts) {
            part.remove(removalReason);
        }
        super.remove(removalReason);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lSteps > 0) {
            this.lSteps = 0;
            this.absMoveTo(this.lx, this.ly, this.lz, (float) this.lyr, (float) this.lxr);
        }
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public Entity getHighestRider() {
        Entity entity;
        for (entity = this; entity.isVehicle(); entity = entity.getFirstPassenger()) {
        }
        return entity;
    }

    @Override
    public double getPassengersRidingOffset() {
        double d = 0.315D + getRenderOffGroundAmount(1.0F);
        if (this.getSkaterPose() == SkaterPose.KICKFLIP) {
            d += 0.4D * getSkaterPoseProgress(1.0F);
        }
        return d;
    }

    public void setStopMovementFlag(boolean b) {
        this.entityData.set(STOP_MOVEMENT_FLAG, b);
    }

    public void tickPlayerRider(Player passenger) {
        float yawDeviate = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
        boolean preserveForwards = false;
        if (!level.isClientSide) {
            if (Math.abs(passenger.zza) > 0) {
                if (passenger.zza < 0) {
                    this.setForwards(Math.min(this.getForwards() - 3, 0));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.8F, 1F, 0.8F));
                    this.setStopMovementFlag(true);
                } else {
                    this.setForwards(Math.min(this.getForwards() + getPedallingAddition(), getMaxForwardsTicks()));
                    this.setStopMovementFlag(false);
                    if (this.getPedalAmount() < 1.0F) {
                        this.setPedalAmount(Math.min(1.0F, this.getPedalAmount() + 0.05F));
                    }
                    preserveForwards = true;
                }
                this.setSkaterPose(SkaterPose.PEDAL);
            } else {
                if (!this.trickFlag) {
                    if (this.isGrinding()) {
                        IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.TRICK_GRIND);
                        this.setSkaterPose(SkaterPose.GRIND);
                    } else {
                        if (this.getDeltaMovement().length() > 0.02F) {
                            this.setSkaterPose(this.sprintDown > 0 || !this.isOnGround() ? SkaterPose.CROUCH : SkaterPose.STAND_SIDEWAYS);
                        } else {
                            this.setSkaterPose(SkaterPose.NONE);
                        }
                    }
                }
                this.setPedalAmount(Math.max(0.0F, this.getPedalAmount() - 0.2F));
            }
            if (!preserveForwards) {
                this.setForwards(Math.max(0, this.getForwards() - getForwardsDecay()));
            }
        }
        if (this.getDeltaMovement().horizontalDistance() < 0.03F && this.getSkaterPose() == SkaterPose.PEDAL && this.entityData.get(STOP_MOVEMENT_FLAG)) {
            this.setXRot(approachRotation(this.getXRot(), -60, 30F));
            this.setYRot(approachRotation(this.getYRot(), passenger.yBodyRot, 15F));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
            this.setPedalAmount(0);
        } else {
            float sidewinder = this.getEnchantLevel(IWSEnchantmentRegistry.SIDEWINDER.get()) + 1;
            passenger.setYBodyRot(approachRotation(passenger.yBodyRot, this.getYRot(), 35));
            if (Math.abs(passenger.xxa) > 0 && !trickFlag) {
                this.setZRot(approachRotation(this.getZRot(), (-10 - sidewinder * 15) * Math.signum(passenger.xxa), sidewinder * 10));
            }
            if (this.getSkaterPose() != SkaterPose.NONE) {
                if (Math.abs(yawDeviate) > 25 && !trickFlag) {
                    this.setZRot(approachRotation(this.getZRot(), Math.signum(yawDeviate), sidewinder * 5));
                }
                if (Math.abs(yawDeviate) > 90) {
                    passenger.setYRot(approachRotation(passenger.getYRot(), this.getYRot(), 5));
                }
            }
        }

        if (this.horizontalCollision) {
            double d11 = this.getDeltaMovement().horizontalDistance();
            double d3 = prevDelta.horizontalDistance();
            double d7 = d3 - d11;
            float f1 = (float) (d7 * 10.0D - 5.0D);
            if (f1 > 0.0F && !this.hasEnchant(IWSEnchantmentRegistry.SECURED.get())) {
                ItemStack itemstack = passenger.getItemBySlot(EquipmentSlot.HEAD);
                boolean damageBlocked = false;
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + 3 + this.random.nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            passenger.broadcastBreakEvent(EquipmentSlot.HEAD);
                            passenger.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                        damageBlocked = true;
                    }
                }
                if (!damageBlocked) {
                    IWSAdvancements.trigger(passenger, IWSAdvancements.TAKE_SKATE_DAMAGE);
                    passenger.hurt(IWSDamageTypes.causeSkateDamage(this.level.registryAccess()), 2 + 2 * f1);
                }
                passenger.stopRiding();
            }
        }
    }

    public float getPedallingAddition() {
        return 1F + getEnchantLevel(IWSEnchantmentRegistry.PEDALLING.get()) * 0.5F;
    }

    public void tickSound() {
        if (this.prevSkaterPose != this.getSkaterPose() && getSkaterPoseProgress(1.0F) == 0) {
            this.playSound(IWSSoundRegistry.SKATEBOARD_CHANGE_POSE.get(), 1, 0.8F + this.random.nextFloat() * 0.4F);
        }
        if (this.getSkaterPose() == SkaterPose.PEDAL && !this.entityData.get(STOP_MOVEMENT_FLAG) && soundTimer == 0) {
            soundTimer = 15 + random.nextInt(10);
            this.playSound(IWSSoundRegistry.SKATEBOARD_PEDAL.get(), 1, 0.8F + this.random.nextFloat() * 0.4F);
        }
        if (!this.isSilent() && !this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 67);
        }
        if (soundTimer > 0) {
            soundTimer--;
        }
    }

    public void tickMobRider(Mob passenger) {
        if (!(passenger.getMoveControl() instanceof SkaterMoveControl) && passenger.getMoveControl() != null) {
            passenger.moveControl = new SkaterMoveControl(passenger, passenger.getMoveControl());
        }
        passenger.yBodyRot = this.getYRot();
        passenger.yHeadRot = Mth.clamp(passenger.yHeadRot, passenger.yBodyRot - 90, passenger.yBodyRot + 90);
    }

    public void positionRider(Entity passenger) {
        prevPedalAmount = this.getPedalAmount();
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            if (passenger instanceof Player) {
                tickPlayerRider((Player) passenger);
            } else if (living instanceof Mob) {
                tickMobRider((Mob) passenger);
            }
            if (this.getSkaterPose() != SkaterPose.NONE) {
                living.walkAnimation.setSpeed(0.0F);
            }
            double d0 = this.getY() + this.getPassengersRidingOffset();
            if (living.getType().is(IWSTags.OVERRIDES_SKATEBOARD_POSITIONING)) {
                d0 += living.getMyRidingOffset();
            }
            living.setPos(this.getX(), d0, this.getZ());
        } else {
            super.positionRider(passenger);
        }
    }

    @Override
    protected void removePassenger(Entity entity) {
        super.removePassenger(entity);
        if (entity instanceof Player player && this.hasEnchant(IWSEnchantmentRegistry.INSTANT_RETURN.get())) {
            returnToPlayer = player;
            this.entityData.set(REMOVE_SOON, true);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || this.hasEnchant(IWSEnchantmentRegistry.HARDWOOD.get()) && (!damageSource.is(DamageTypes.OUT_OF_WORLD) && !damageSource.is(DamageTypes.GENERIC) && !(damageSource.getDirectEntity() instanceof Player));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (amount > 0.0F) {
            this.entityData.set(REMOVE_SOON, true);
            if (source.getEntity() instanceof Player player) {
                returnToPlayer = player;
            }
            return true;
        }
        return false;
    }

    @Override
    public void push(Entity entity) {
        super.push(entity);
        if (!this.isPassengerOfSameVehicle(entity) && this.getDeltaMovement().length() > 0.1F && this.isVehicle() && entity instanceof LivingEntity living && getContactDamage() > 0) {
            Entity rider = this.getFirstPassenger();
            if (rider != null && shouldRiderHurtMob(rider, living) && living.hurt(damageSources().indirectMagic(this, rider), getContactDamage())) {
                living.knockback(0.5D, living.getX() - this.getX(), living.getZ() - this.getZ());
                if (living.getHealth() <= 0.0D && this.hasEnchant(IWSEnchantmentRegistry.BASHING.get())) {
                    IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.SKATE_BASHING);
                }
            }
        }
    }

    private boolean shouldRiderHurtMob(Entity rider, LivingEntity living) {
        if (this.getFirstPassenger() instanceof Monster) {
            return !(living instanceof Monster);
        }
        return !living.isAlliedTo(rider) && !rider.isAlliedTo(living);
    }

    public int getContactDamage() {
        int i = this.getEnchantLevel(IWSEnchantmentRegistry.BASHING.get()) * 2;
        if (this.getFirstPassenger() instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.HEAD).is(IWSItemRegistry.SPIKED_SKATER_HELMET.get())) {
            i += 2;
        }
        return i;
    }

    public float getRenderOffGroundAmount(float partialTick) {
        if (this.skateboardData.getWheelType() == SkateboardWheels.HOVER) {
            return (0.8F + (float) Math.sin((tickCount + partialTick) * 0.1F) * 0.5F) * 0.6F;
        }
        return 0.0F;
    }

    public void checkDespawn() {
        if (this.isMobSpawned() && !this.isVehicle()) {
            Entity entity = this.level.getNearestPlayer(this, -1.0D);
            if (entity != null) {
                double d0 = entity.distanceToSqr(this);
                int i = this.getType().getCategory().getDespawnDistance();
                if (d0 > i * i) {
                    this.discard();
                }
                int k = this.getType().getCategory().getNoDespawnDistance();
                int l = k * k;
                if (this.random.nextInt(800) == 0 && d0 > l) {
                    this.discard();
                }
            }
        }
    }


    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    public double getTrailVar(int pointer, int index, float partialTick) {
        if (this.isRemoveLogic()) {
            partialTick = 1.0F;
        }
        int i = this.trailPosPointer - pointer & 63;
        int j = this.trailPosPointer - pointer - 1 & 63;
        double d0 = this.trailPositions[j][index];
        double d1 = Mth.wrapDegrees(this.trailPositions[i][index] - d0);
        return d0 + d1 * partialTick;
    }

    public Vec3 getTrailOffset(int offset, float partialTick) {
        double d0 = Mth.lerp(partialTick, this.xOld, this.getX());
        double d1 = Mth.lerp(partialTick, this.yOld, this.getY());
        double d2 = Mth.lerp(partialTick, this.zOld, this.getZ());
        return new Vec3(this.getTrailVar(offset, 0, partialTick) - d0, this.getTrailVar(offset, 1, partialTick) - d1, this.getTrailVar(offset, 2, partialTick) - d2);
    }

    public void onInteractPacket(Entity interacter, int type) {
        switch (type) {
            case 0: //interact
                if (interacter instanceof Player player) {
                    interact(player, player.getUsedItemHand());
                }
                break;
            case 1: //hurt
                this.entityData.set(REMOVE_SOON, true);
                break;
        }
    }

    public void onKeyPacket(Entity keyPresser, int type) {
        switch (type) {
            case 0:
                sprintDown = 10;
                break;
        }
    }

    @Override
    public void onPlayerJump(int barAmount) {
    }


    @Override
    public boolean canJump() {
        return this.getOnGroundProgress(1.0F) >= 0.1F && this.getSkaterPose().allowJumping() && this.getSkaterPoseProgress(1.0F) >= 0.5F;
    }

    @Override
    public float getStepHeight() {
        return this.hasEnchant(IWSEnchantmentRegistry.CLAMBERING.get()) ? 1.0F : 0.51F;
    }

    @Override
    public void handleStartJump(int barAmount) {
        trickFlag = true;
        int aerial = this.getEnchantLevel(IWSEnchantmentRegistry.AERIAL.get());
        if (this.getSlowMotionLevel() > 0 && IWannaSkateMod.COMMON_CONFIG.enableSlowMotion.get()) {
            IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.SLOW_MOTION);
            enterSlowMotion();
        }

        if (barAmount >= 80) {
            this.setSkaterPose(SkaterPose.KICKFLIP);
            IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.TRICK_KICKFLIP);
            this.jumpFor = aerial * 2 + 8;
        } else {
            this.setSkaterPose(SkaterPose.OLLIE);
            IWSAdvancements.trigger(this.getFirstPassenger(), IWSAdvancements.TRICK_OLLIE);
            this.jumpFor = aerial * 2 + 4 + barAmount / 20;
        }
        this.playSound(IWSSoundRegistry.SKATEBOARD_JUMP_START.get(), 1, 0.8F + this.random.nextFloat() * 0.4F);
        sprintDown = this.jumpFor;
    }

    private void enterSlowMotion() {
        if (!level.isClientSide && level instanceof ServerLevel) {
            float speed = this.getSlowMotionLevel() + 1;
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(level.getServer());
            for (TickRateModifier modifier : tracker.tickRateModifierList) {
                if (modifier instanceof LocalEntityTickRateModifier entityTick && entityTick.getEntityId() == this.getId()) {
                    return;
                }
            }
            tracker.addTickRateModifier(new LocalEntityTickRateModifier(this.getId(), this.getType(), IWannaSkateMod.COMMON_CONFIG.slowMotionDistance.get(), this.level.dimension(), 200, speed));
        }
    }

    private int getSlowMotionLevel() {
        if (this.getFirstPassenger() instanceof Player player) {
            if (player.hasEffect(IWSEffectRegistry.HIGH_OCTANE.get())) {
                MobEffectInstance instance = player.getEffect(IWSEffectRegistry.HIGH_OCTANE.get());
                return instance == null ? 1 : instance.getAmplifier() + 1;
            }
        }
        return 0;
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMod, DamageSource source) {
        float listenDistance = 5.0F + 2.0F * this.getEnchantLevel(IWSEnchantmentRegistry.AERIAL.get());
        if (this.isVehicle() && fallDistance > listenDistance && !this.hasEnchant(IWSEnchantmentRegistry.SECURED.get())) {
            for (Entity entity : this.getPassengers()) {
                IWSAdvancements.trigger(entity, IWSAdvancements.TAKE_SKATE_DAMAGE);
                entity.causeFallDamage(fallDistance, damageMod, IWSDamageTypes.causeSkateDamage(this.level.registryAccess()));
            }
            if (fallDistance > listenDistance + 3) {
                this.ejectPassengers();
            }
        }
        return false;
    }


    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return (this.hasEnchant(IWSEnchantmentRegistry.SURFING.get()) || this.hasEnchant(IWSEnchantmentRegistry.BENTHIC.get())) && type == ForgeMod.WATER_TYPE.get();
    }

    public boolean hasGlint() {
        return this.getItemStack().hasFoil();
    }

    public boolean hasEnchant(Enchantment enchantment) {
        return getEnchantLevel(enchantment) > 0;
    }

    public int getEnchantLevel(Enchantment enchantment) {
        return this.enchantments == null || !this.enchantments.containsKey(enchantment) ? 0 : this.enchantments.get(enchantment);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 67) {
            IWannaSkateMod.PROXY.onEntityStatus(this, id);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Nullable
    public Component getOnMountMessage() {
        if (this.getSlowdown() < 0.6F) {
            return Component.translatable("entity.iwannaskate.skateboard.ground_warning");
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.getItemStack().copy();
    }


    @Override
    public boolean shouldRender(double x, double y, double z) {
        boolean prev = super.shouldRender(x, y, z);
        return prev || this.isVehicle() && this.getFirstPassenger() != null && this.getFirstPassenger().shouldRender(x, y, z);
    }

    @Override
    public boolean isTimeModificationValid(TickRateModifier tickRateModifier) {
        return this.getOnGroundProgress(0F) < 0.9F;
    }

    public EntityDimensions getDimensions(Pose pose) {
        if (size.height != getBoardHeight()) {
            size = EntityDimensions.scalable(size.width, getBoardHeight());
        }
        return size;
    }

    public float getBoardHeight() {
        if (skateboardData != null && skateboardData.getWheelType() == SkateboardWheels.HOVER) {
            return 0.6F;
        }
        return 0.3125F;
    }

    public boolean canWheelsMakeSound() {
        return skateboardData == null || !skateboardData.getWheelType().hideTrucks();
    }
}