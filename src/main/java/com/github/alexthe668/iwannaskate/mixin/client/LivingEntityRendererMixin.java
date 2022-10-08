package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.client.model.ModelRootRegistry;
import com.github.alexthe668.iwannaskate.client.render.entity.SkatingModelPositioner;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.potion.IWSEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Shadow
    protected EntityModel model;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/EntityModel;F)V"},
            remap = true,
            at = @At(value = "RETURN")
    )
    protected void iws_constructor(EntityRendererProvider.Context context, EntityModel model, float f, CallbackInfo ci) {
        ModelRootRegistry.onConstructRenderer(model);
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V"},
            remap = true,
            at = @At(value = "RETURN")
    )
    protected void iws_setupRotations(LivingEntity livingEntity, PoseStack poseStack, float ageInTicks, float bodyYRot, float partialTick, CallbackInfo ci) {
        if(livingEntity.getVehicle() instanceof SkateboardEntity skateboard){
            SkatingModelPositioner.setupSkaterRotations(livingEntity, model, skateboard, poseStack, bodyYRot, partialTick);
        }
        if(livingEntity.hasEffect(IWSEffectRegistry.OVERCAFFEINATED.get())){
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float)(Math.cos((double)ageInTicks * 3.25D) * Math.PI * (double)0.4F)));
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            remap = true,

            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void iws_render_setupAnim_before(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if(SkatingModelPositioner.doesChangeModel(livingEntity)){
            SkatingModelPositioner.saveModel(model);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            remap = true,

            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void iws_render_setupAnim_after(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if(SkatingModelPositioner.doesChangeModel(livingEntity) && livingEntity.getVehicle() instanceof SkateboardEntity skateboard){
            SkatingModelPositioner.setupSkaterAnimations(model, livingEntity, skateboard, partialTicks);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
            remap = true,
            at = @At(value = "RETURN")
    )
    protected void iws_render_renderToBuffer(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if(SkatingModelPositioner.doesChangeModel(livingEntity)) {
            SkatingModelPositioner.restoreModel(model);
        }
    }
}
