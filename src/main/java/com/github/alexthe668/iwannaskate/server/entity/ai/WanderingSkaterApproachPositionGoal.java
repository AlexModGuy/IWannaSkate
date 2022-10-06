package com.github.alexthe668.iwannaskate.server.entity.ai;

import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WanderingSkaterApproachPositionGoal extends Goal {
    final WanderingSkaterEntity trader;
    final double stopDistance;
    final double speedModifier;

    public WanderingSkaterApproachPositionGoal(WanderingSkaterEntity skater, double stopDistance, double speed) {
        this.trader = skater;
        this.stopDistance = stopDistance;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public void stop() {
        this.trader.setWanderTarget(null);
        trader.getNavigation().stop();
    }

    public boolean canUse() {
        BlockPos blockpos = this.trader.getWanderingSkaterTarget();
        return blockpos != null && this.isTooFarAway(blockpos, this.stopDistance);
    }

    public void tick() {
        BlockPos blockpos = this.trader.getWanderingSkaterTarget();
        if (blockpos != null && trader.getNavigation().isDone()) {
            if (this.isTooFarAway(blockpos, 10.0D)) {
                Vec3 vec3 = (new Vec3((double) blockpos.getX() - this.trader.getX(), (double) blockpos.getY() - this.trader.getY(), (double) blockpos.getZ() - this.trader.getZ())).normalize();
                Vec3 vec31 = vec3.scale(10.0D).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());
                trader.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, this.speedModifier);
            } else {
                trader.getNavigation().moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.speedModifier);
            }
        }

    }

    private boolean isTooFarAway(BlockPos p_35904_, double p_35905_) {
        return !p_35904_.closerToCenterThan(this.trader.position(), p_35905_);
    }
}
