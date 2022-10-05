package com.github.alexthe668.iwannaskate.mixin;

import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.entity.SlowableEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements SlowableEntity {

    private static final EntityDataAccessor<Integer> SLOWABLE_TICKRATE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);

    @Shadow
    public Level level;
    @Final
    @Shadow
    protected SynchedEntityData entityData;

    @Override
    public void setTickRate(int tickRate) {
        entityData.set(SLOWABLE_TICKRATE, tickRate);
    }

    @Override
    public int getTickRate() {
        int i = entityData.get(SLOWABLE_TICKRATE);
        return i <= 0 ? 20 : i;
    }

    @Override
    public boolean cancelTick(int serverTick) {
        return serverTick % (20 / getTickRate()) == 1;
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    protected void iws_entity_constructor(EntityType entityType, Level level, CallbackInfo ci) {
        entityData.define(SLOWABLE_TICKRATE, 20);
    }

}
