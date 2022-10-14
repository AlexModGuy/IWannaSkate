package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.client.render.entity.SkatingModelPositioner;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {


    public CapeLayerMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/layers/CapeLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_renderCapeLayer(PoseStack poseStack, MultiBufferSource bufferIn, int i, AbstractClientPlayer player, float f1, float f2, float partialTicks, float f4, float f5, float f6, CallbackInfo ci) {
        if(player.getVehicle() instanceof SkateboardEntity skateboard) {
            ci.cancel();
            if (player.isCapeLoaded() && !player.isInvisible() && player.isModelPartShown(PlayerModelPart.CAPE) && player.getCloakTextureLocation() != null) {
                ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemstack.is(Items.ELYTRA)) {
                    poseStack.pushPose();
                    poseStack.translate(0.0D, 0.0D, 0.25D);
                    float poseProgress = skateboard.getSkaterPoseProgress(partialTicks);
                    float priorPoseProgress = 1F - poseProgress;
                    SkatingModelPositioner.rotateCapeForPose(player, poseStack, partialTicks, skateboard, skateboard.getPrevSkaterPose(), priorPoseProgress);
                    SkatingModelPositioner.rotateCapeForPose(player, poseStack, partialTicks, skateboard, skateboard.getSkaterPose(), poseProgress);
                    VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entitySolid(player.getCloakTextureLocation()));
                    getParentModel().renderCloak(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            }
        }

    }
}
