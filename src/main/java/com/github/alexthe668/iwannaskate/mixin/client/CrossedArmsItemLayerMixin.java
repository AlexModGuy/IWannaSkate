package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.server.entity.HasAnimationFlags;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossedArmsItemLayer.class)
public class CrossedArmsItemLayerMixin {

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/layers/CrossedArmsItemLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    public void iws_render(PoseStack poseStack, MultiBufferSource bufferIn, int lightLevel, LivingEntity livingEntity, float limbSwing, float limgSwingAmount, float partialTicks, float bob, float yRot, float xRot, CallbackInfo ci) {
        if (livingEntity instanceof Villager villager) {
            float ageInTicks = villager.tickCount + partialTicks;
            ItemStack stack = villager.getItemInHand(InteractionHand.MAIN_HAND);
            if (villager instanceof HasAnimationFlags animationFlags) {
                if (stack != null && stack.is(IWSItemRegistry.ENERGY_DRINK.get()) && animationFlags.getIWSAnimationFlags() == 1) {
                    float bounce = (float) Math.abs(Math.sin(ageInTicks * 0.4F)) * 0.25F;
                    poseStack.mulPose(Axis.XN.rotationDegrees(25F));
                    poseStack.translate(0, bounce - 0.1F, 0.075F);
                    if(villager.isBaby()){
                        poseStack.scale(1.5F, 1.5F, 1.5F);
                    }
                }
            }
        }
    }

}
