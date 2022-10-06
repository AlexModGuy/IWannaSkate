package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.model.IWSModelLayers;
import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WanderingSkaterRenderer extends MobRenderer<WanderingSkaterEntity, VillagerModel<WanderingSkaterEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/wandering_skater.png");

    public WanderingSkaterRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(IWSModelLayers.WANDERING_SKATER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new CrossedArmsItemLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new SkateboardItemLayer(this, context.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(WanderingSkaterEntity trader) {
        return TEXTURE;
    }

    protected void scale(WanderingSkaterEntity trader, PoseStack poseStack, float f) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    class SkateboardItemLayer extends RenderLayer<WanderingSkaterEntity, VillagerModel<WanderingSkaterEntity>> {
        private final ItemInHandRenderer itemInHandRenderer;

        public SkateboardItemLayer(WanderingSkaterRenderer renderer, ItemInHandRenderer p_234819_) {
            super(renderer);
            this.itemInHandRenderer = p_234819_;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, WanderingSkaterEntity skater, float f, float f1, float f2, float f3, float f4, float f5) {
            poseStack.pushPose();
            poseStack.translate(0.0D, (double)0.1F, (double)0.1F);
            poseStack.mulPose(Vector3f.ZN.rotationDegrees(35.0F));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            poseStack.mulPose(Vector3f.YN.rotationDegrees(90.0F));
            ItemStack itemstack = skater.getItemBySlot(EquipmentSlot.OFFHAND);
            this.itemInHandRenderer.renderItem(skater, itemstack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, poseStack, bufferSource, i);
            poseStack.popPose();
        }
    }
}