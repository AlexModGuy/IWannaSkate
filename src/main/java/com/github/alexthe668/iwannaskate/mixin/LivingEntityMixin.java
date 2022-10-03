package com.github.alexthe668.iwannaskate.mixin;

import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/LivingEntity;isInWall()Z"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_isInWall(CallbackInfoReturnable<Boolean> cir) {
        if(this.getVehicle() instanceof SkateboardEntity){
            cir.setReturnValue(false);
        }
    }
}
