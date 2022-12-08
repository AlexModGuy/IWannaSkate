package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.model.SkateboardModel;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.mojang.math.Axis;
import org.joml.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class SkateboardRenderer extends EntityRenderer<SkateboardEntity> {

    private static final SkateboardModel SKATEBOARD_MODEL = new SkateboardModel();
    private static final ResourceLocation RAINBOW_TRAIL_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/wheels/rainbow_trail.png");
    private static final ResourceLocation AESTHETIC_TRAIL_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/wheels/aesthetic_trail.png");
    private LightningRender lightningRender = new LightningRender();
    private LightningBoltData.BoltRenderInfo lightningBoltData = new LightningBoltData.BoltRenderInfo(1.3F, 0.15F, 0.5F, 0.25F, new Vector4f(0.1F, 0.1F, 0.1F, 0.5F), 0.45F);

    public SkateboardRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public Vec3 getRenderOffset(SkateboardEntity entity, float partialTicks) {
        return Vec3.ZERO;
    }

    public void render(SkateboardEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float ageInTicks = entity.tickCount + partialTicks;
        float skateYaw = entity.getViewYRot(partialTicks);
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - skateYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.translate(0, -entity.getRenderOffGroundAmount(partialTicks), 0);
        poseStack.pushPose();
        SKATEBOARD_MODEL.setupAnim(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
        if(entity.getSkateboardData().getWheelType().hasTrail()){
            Vec3 from = Vec3.ZERO;
            Vec3 to = entity.getTrailOffset(8, partialTicks).multiply(1, 0.3, 1);
            boolean rainbow = entity.getSkateboardData().getWheelType() == SkateboardWheels.RAINBOW;
            renderRainbowFromWheel(entity, from, to, 2, poseStack, buffer, skateYaw, rainbow);
            renderRainbowFromWheel(entity, from, to, 3, poseStack, buffer, skateYaw, rainbow);
            renderRainbowFromWheel(entity, from, to, 0, poseStack, buffer, skateYaw, rainbow);
            renderRainbowFromWheel(entity, from, to, 1, poseStack, buffer, skateYaw, rainbow);
        }
        SkateboardTexturer.renderBoard(SKATEBOARD_MODEL, entity.getSkateboardData(), poseStack, buffer, packedLight, entity.hasGlint());
        if(entity.getSkateboardData().getWheelType() == SkateboardWheels.SHOCKING){
            renderLightningFromWheel(entity, 0, poseStack, buffer, partialTicks);
            renderLightningFromWheel(entity, 1, poseStack, buffer, partialTicks);
            renderLightningFromWheel(entity, 2, poseStack, buffer, partialTicks);
            renderLightningFromWheel(entity, 3, poseStack, buffer, partialTicks);
            lightningRender.render(partialTicks, poseStack, buffer);
        }
        poseStack.popPose();
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderLightningFromWheel(SkateboardEntity entity, int wheel, PoseStack poseStack, MultiBufferSource buffer, float partialTicks) {
        Vec3 strike = entity.lightningPositions[wheel];
        String wheelId = "skateboard" + entity.getId() + "_wheel_" + wheel;
        if(strike != null){
            Vec3 wheelPos = translateToWheel(wheel);
            LightningBoltData.BoltRenderInfo lightningBoltData = new LightningBoltData.BoltRenderInfo(0.05F, 0.2F, 0.5F, 0.85F, new Vector4f(0.3F, 0.45F, 0.6F, 0.85F), 0.1F);
            LightningBoltData bolt = new LightningBoltData(lightningBoltData, wheelPos, wheelPos.add(strike.scale(0.7F)).add(entity.getDeltaMovement()), 7)
                    .size(0.05F)
                    .lifespan(4)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE);
            lightningRender.update(wheelId, bolt, partialTicks);
        }
    }

    private static Vec3 translateToWheel(int wheel){
        PoseStack modelTranslateStack = new PoseStack();
        SKATEBOARD_MODEL.root.translateAndRotate(modelTranslateStack);
        SKATEBOARD_MODEL.board.translateAndRotate(modelTranslateStack);
        float up = 0;
        boolean left = false;
        if(wheel == 0){
            SKATEBOARD_MODEL.frontAxel.translateAndRotate(modelTranslateStack);
            SKATEBOARD_MODEL.leftFrontWheel.translateAndRotate(modelTranslateStack);
            left = true;
            up = 0.01F;
        }else if(wheel == 1){
            SKATEBOARD_MODEL.frontAxel.translateAndRotate(modelTranslateStack);
            SKATEBOARD_MODEL.rightFrontWheel.translateAndRotate(modelTranslateStack);
            up = 0.015F;
        }else if(wheel == 2){
            SKATEBOARD_MODEL.backAxel.translateAndRotate(modelTranslateStack);
            SKATEBOARD_MODEL.leftBackWheel.translateAndRotate(modelTranslateStack);
            left = true;
            up = -0.015F;
        }else if(wheel == 3){
            SKATEBOARD_MODEL.backAxel.translateAndRotate(modelTranslateStack);
            SKATEBOARD_MODEL.rightBackWheel.translateAndRotate(modelTranslateStack);
            up = -0.005F;
        }
        Vector4f bodyOffsetVec = new Vector4f(left ? -0.08F : 0.08F, 0F, 0F, 1.0F);
        bodyOffsetVec.mul(modelTranslateStack.last().pose());
        Vec3 wheelOffset = new Vec3(bodyOffsetVec.x(), bodyOffsetVec.y(), bodyOffsetVec.z());
        modelTranslateStack.popPose();
        return wheelOffset.add(0, -up, 0);
    }

    private static void renderRainbowFromWheel(SkateboardEntity entity, Vec3 from, Vec3 to, int wheel, PoseStack poseStack, MultiBufferSource bufferSource, float skateYaw, boolean rainbow){
        Vec3 sub = from.subtract(to);
        double d = sub.horizontalDistance();
        float rotY = (float) (Mth.atan2(sub.x, sub.z) * (double) (180F / (float) Math.PI));
        float rotX = (float) (-(Mth.atan2(sub.y, d) * (double) (180F / (float) Math.PI))) - 90.0F;
        Vec3 wheelOffset = translateToWheel(wheel);

        poseStack.pushPose();
        poseStack.translate(wheelOffset.x, wheelOffset.y, wheelOffset.z);
        poseStack.scale(-1.0F, 1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(skateYaw));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer rainbowConsumer = bufferSource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(rainbow ? RAINBOW_TRAIL_TEXTURE : AESTHETIC_TRAIL_TEXTURE, false));
        float height = Mth.clamp((float)(sub.length() * 0.5F), 0, 1.2F);
        float moveAlong = (entity.tickCount + Minecraft.getInstance().getFrameTime()) * -0.1F;
        rainbowVertex(rainbowConsumer, matrix4f, matrix3f, 240, 0.3F,  height, 0, moveAlong + 1, 0);
        rainbowVertex(rainbowConsumer, matrix4f, matrix3f, 240, 0.7F,  height, 1, moveAlong + 1, 0);
        rainbowVertex(rainbowConsumer, matrix4f, matrix3f, 240, 0.7F, 0, 1, moveAlong, 1);
        rainbowVertex(rainbowConsumer, matrix4f, matrix3f, 240, 0.3F, 0, 0, moveAlong, 1);
        poseStack.popPose();
    }

    private static void rainbowVertex(VertexConsumer p_114090_, Matrix4f p_114091_, Matrix3f p_114092_, int p_114093_, float p_114094_, float p_114095_, float p_114096_, float p_114097_, float alpha) {
        p_114090_.vertex(p_114091_, p_114094_ - 0.5F, (float)p_114095_, 0.0F).color(1F, 1F, 1F,  alpha).uv((float)p_114096_, (float)p_114097_).overlayCoords(NO_OVERLAY).uv2(240).normal(p_114092_, 0.0F, -1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(SkateboardEntity skateboard) {
        return null;
    }
}
