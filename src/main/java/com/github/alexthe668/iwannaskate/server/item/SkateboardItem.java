package com.github.alexthe668.iwannaskate.server.item;

import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class SkateboardItem extends BaseSkateboardItem {

    private static final Predicate<Entity> PICKABLE_ENTITIES = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    public SkateboardItem(Properties properties) {
        super(properties);
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        //hide enchantments so that they can be truncated
        return ItemStack.TooltipPart.ENCHANTMENTS.getMask();
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (raytraceresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vector3d = player.getViewVector(1.0F);
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vector3d.scale(player.getBlockReach())).inflate(1.0D), PICKABLE_ENTITIES);
            if (!list.isEmpty()) {
                Vec3 vector3d1 = player.getEyePosition(1.0F);

                for (Entity entity : list) {
                    AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (axisalignedbb.contains(vector3d1)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                Vec3 vec3 = raytraceresult.getLocation();
                SkateboardEntity skateboard = IWSEntityRegistry.SKATEBOARD.get().create(level);
                skateboard.setItemStack(itemstack.copy());
                skateboard.setPos(vec3);
                skateboard.setYRot(player.getYRot());
                skateboard.setXRot(-70);
                if (!level.noCollision(skateboard, skateboard.getBoundingBox().inflate(-0.1D))) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(skateboard);

                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    @Override
    public boolean canHoverOver(ItemStack itemStack) {
        return SkateboardData.fromStack(itemStack).hasBanner();
    }
}
