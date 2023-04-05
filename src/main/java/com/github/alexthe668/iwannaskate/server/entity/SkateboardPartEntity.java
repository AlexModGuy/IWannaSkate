package com.github.alexthe668.iwannaskate.server.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.network.SkateboardPartMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.entity.PartEntity;

import java.util.Optional;

public class SkateboardPartEntity extends PartEntity<SkateboardEntity> {

    private EntityDimensions size;
    public float scale = 1;

    public SkateboardPartEntity(SkateboardEntity parent) {
        super(parent);
        this.blocksBuilding = true;
        this.size = EntityDimensions.scalable(0.6F, 0.3125F);
    }

    public EntityDimensions getDimensions(Pose pose) {
        SkateboardEntity parent = this.getParent();
        if (parent != null && size.height != parent.getBoardHeight()) {
            size = EntityDimensions.scalable(size.width, parent.getBoardHeight());
        }
        return size;
    }

   @Override
   public boolean fireImmune() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        SkateboardEntity parent = this.getParent();
        if (parent == null) {
            return InteractionResult.PASS;
        } else {
            if(player.level.isClientSide){
                IWannaSkateMod.sendMSGToServer(new SkateboardPartMessage(parent.getId(), player.getId(), 0));
            }
            return parent.interact(player, hand);
        }
    }

    @Override
    public boolean save(CompoundTag tag) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        SkateboardEntity parent = this.getParent();
        return parent != null && parent.canBeCollidedWith();
    }


    @Override
    public boolean isPickable() {
        SkateboardEntity parent = this.getParent();
        return parent != null && parent.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        SkateboardEntity parent = this.getParent();
        if(!this.isInvulnerableTo(source) && parent != null){
            Entity player = source.getEntity();
            if(player != null && player.level.isClientSide){
                IWannaSkateMod.sendMSGToServer(new SkateboardPartMessage(parent.getId(), player.getId(), 1));
            }
            parent.hurt(source, amount);
        }
        return false;
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    public boolean shouldBeSaved() {
        return false;
    }

    public float getHeightBelow(Vec3 boardPos, float height) {
        SkateboardEntity parent = getParent();
        if(parent == null){
            return 0.0F;
        }
        BlockPos pos = BlockPos.containing(boardPos.x, boardPos.y + 0.001D, boardPos.z);
        float dist = this.getDistance(parent.level, pos, boardPos);
        if ((double)(1.0F - dist) < 0.001D) {
            dist = this.getDistance(parent.level, pos.below(), boardPos) + (float)boardPos.y % 1.0F;
        } else {
            dist = (float)((double)dist - (1.0D - boardPos.y % 1.0D));
        }
        if (height <= dist && parent.isOnGround()) {
            return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
        } else {
            return height == dist ? height : Math.max(height - this.getRiseSpeed(), dist);
        }
    }

    private float getDistance(Level world, BlockPos pos, Vec3 position) {
        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getCollisionShape(world, pos);
        if(shape.isEmpty()){
            return 1.0F;
        }
        Optional<Vec3> closest = shape.closestPointTo(position);
        if(closest.isEmpty()){
            return 1.0F;
        }else{
            float closestY = Math.min((float)closest.get().y, 1.0F);
            return position.y < 0.0 ? closestY : 1.0F - closestY;
        }
    }

    protected float getFallSpeed() {
        return 1;
    }

    protected float getRiseSpeed() {
        return 1;
    }
}