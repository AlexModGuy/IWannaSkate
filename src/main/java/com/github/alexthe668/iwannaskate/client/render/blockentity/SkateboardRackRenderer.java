package com.github.alexthe668.iwannaskate.client.render.blockentity;

import com.github.alexthe668.iwannaskate.server.blockentity.SkateboardRackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class SkateboardRackRenderer implements BlockEntityRenderer<SkateboardRackBlockEntity> {

    public SkateboardRackRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
    }

    @Override
    public void render(SkateboardRackBlockEntity entity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack topStack = entity.getItem(0);
        ItemStack bottomStack = entity.getItem(1);
        Direction direction = entity.getBlockAngle();
        float mouseOver1 = entity.getHoverOver(true, partialTicks);
        float mouseOver2 = entity.getHoverOver(false, partialTicks);
        float x1 = 45 - mouseOver1 * 15;
        float x2 = 45 - mouseOver2 * 15;
        if(direction.getAxis() == Direction.Axis.Z){
            x1 = 180 - x1;
            x2 = 180 - x2;
        }
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        matrixStackIn.mulPose(new Quaternion(Vector3f.YP, direction.toYRot(), true));
        matrixStackIn.pushPose();
        if (!topStack.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0.4F,  0.1F * mouseOver1);
            matrixStackIn.mulPose(new Quaternion(Vector3f.XN,  x1, true));
            matrixStackIn.mulPose(new Quaternion(Vector3f.ZP, 90, true));
            matrixStackIn.scale(1.15F, 1.15F, 1.15F);
            Minecraft.getInstance().getItemRenderer().renderStatic(topStack, ItemTransforms.TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
        if (!bottomStack.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -0.1F + 0.05F * mouseOver2, 0.1F + 0.05F * mouseOver2);
            matrixStackIn.mulPose(new Quaternion(Vector3f.XN,  x2, true));
            matrixStackIn.mulPose(new Quaternion(Vector3f.ZP, 90, true));
            matrixStackIn.scale(1.15F, 1.15F, 1.15F);
            Minecraft.getInstance().getItemRenderer().renderStatic(bottomStack, ItemTransforms.TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
        matrixStackIn.popPose();


    }
}