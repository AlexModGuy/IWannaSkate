package com.github.alexthe668.iwannaskate.mixin;

import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin {

    @Shadow
    @Final
    private Map<BlockState, VoxelShape> shapeByIndex;

    @Inject(
            method = {"Lnet/minecraft/world/level/block/WallBlock;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if(state.is(IWSTags.GRINDS) && context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof SkateboardEntity){
            cir.setReturnValue(shapeByIndex.get(state));
        }
    }
}
