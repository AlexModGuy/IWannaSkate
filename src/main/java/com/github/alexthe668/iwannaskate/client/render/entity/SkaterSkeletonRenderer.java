package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe668.iwannaskate.client.model.IWSModelLayers;
import com.github.alexthe668.iwannaskate.server.entity.SkaterSkeletonEntity;
import com.github.alexthe668.iwannaskate.server.item.BaseSkateboardItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;

public class SkaterSkeletonRenderer extends MobRenderer<SkaterSkeletonEntity, SkeletonModel<SkaterSkeletonEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("iwannaskate:textures/entity/skater_skeleton.png");

    public SkaterSkeletonRenderer(EntityRendererProvider.Context context) {
        this(context, IWSModelLayers.SKATER_SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public SkaterSkeletonRenderer(EntityRendererProvider.Context context, ModelLayerLocation body, ModelLayerLocation armor1, ModelLayerLocation armor2) {
        super(context, new SkeletonModel<>(context.bakeLayer(body)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(context.bakeLayer(armor1)), new SkeletonModel(context.bakeLayer(armor2))));
        this.addLayer(new ItemInHandLayer(this, context.getItemInHandRenderer()){

            @Override
            protected void renderArmWithItem(LivingEntity livingEntity, ItemStack stack, ItemTransforms.TransformType transformType, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int i) {
                if (!stack.isEmpty()) {
                    poseStack.pushPose();
                    ((SkeletonModel)(this.getParentModel())).translateToHand(arm, poseStack);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    boolean flag = arm == HumanoidArm.LEFT;
                    float skateboardModifier = stack.getItem() instanceof BaseSkateboardItem ? 0.0625F : 0F;
                    poseStack.translate((float)(flag ? -1 : 1) * (0.0625F + skateboardModifier), 0.125D, -0.625D);
                    context.getItemInHandRenderer().renderItem(livingEntity, stack, transformType, flag, poseStack, buffer, i);
                    poseStack.popPose();
                }
            }
        });
    }

    public ResourceLocation getTextureLocation(SkaterSkeletonEntity skeleton) {
        return TEXTURE;
    }

    protected boolean isShaking(SkaterSkeletonEntity skeleton) {
        return skeleton.isShaking();
    }
}