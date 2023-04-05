package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.model.IWSModelLayers;
import com.github.alexthe668.iwannaskate.client.model.WanderingSkaterModel;
import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WanderingSkaterRenderer extends MobRenderer<WanderingSkaterEntity, WanderingSkaterModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/wandering_skater.png");

    public WanderingSkaterRenderer(EntityRendererProvider.Context context) {
        super(context, new WanderingSkaterModel(context.bakeLayer(IWSModelLayers.WANDERING_SKATER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new SkateboardItemLayer(this, context.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(WanderingSkaterEntity trader) {
        return TEXTURE;
    }

    protected void scale(WanderingSkaterEntity trader, PoseStack poseStack, float f) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    class SkateboardItemLayer extends RenderLayer<WanderingSkaterEntity, WanderingSkaterModel> {
        private final ItemInHandRenderer itemInHandRenderer;

        public SkateboardItemLayer(WanderingSkaterRenderer renderer, ItemInHandRenderer p_234819_) {
            super(renderer);
            this.itemInHandRenderer = p_234819_;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, WanderingSkaterEntity skater, float f, float f1, float f2, float f3, float f4, float f5) {
            poseStack.pushPose();
            float swing = skater.getAttackingProgress(f2);
            float swing2 = (float) Math.sin(swing * Math.PI);
            this.getParentModel().translateToArms(poseStack);
            poseStack.translate( -swing2 * 0.5F + swing * 0.2F, (double)0.1F - 1.2F * swing2 + swing * 0.2F, (double)0.05 - swing * 1.1F);
            poseStack.mulPose(Axis.ZN.rotationDegrees(35.0F * (1 - swing)));
            poseStack.mulPose(Axis.XP.rotationDegrees(170.0F + swing * 100.0F));
            poseStack.mulPose(Axis.YN.rotationDegrees(100.0F * (1 - swing)));
            ItemStack offhand = skater.getItemBySlot(EquipmentSlot.OFFHAND);
            this.itemInHandRenderer.renderItem(skater, offhand, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferSource, i);
            poseStack.popPose();

            poseStack.pushPose();
            this.getParentModel().translateToArms(poseStack);
            poseStack.translate(0.0D, (double)0.3F, (double)-0.35F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            ItemStack itemstack = skater.getItemBySlot(EquipmentSlot.MAINHAND);
            this.itemInHandRenderer.renderItem(skater, itemstack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, i);
            poseStack.popPose();
        }
    }
}