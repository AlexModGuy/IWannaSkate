package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.model.ModelPartType;
import com.github.alexthe668.iwannaskate.client.model.ModelPartWrapper;
import com.github.alexthe668.iwannaskate.client.model.ModelRootRegistry;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.entity.SkaterPose;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SkatingModelPositioner {

    private static final List<ModelPositions> models = new ArrayList<>();
    private static final List<EntityModel> modelsNeedingRestoration = new ArrayList<>();

    public static void setupSkaterRotations(LivingEntity livingEntity, EntityModel model, SkateboardEntity skateboard, PoseStack poseStack, float bodyYRot, float partialTick) {
        float poseProgress = skateboard.getSkaterPoseProgress(partialTick);
        float priorPoseProgress = 1F - poseProgress;
        rotateForPose(livingEntity, model, poseStack, partialTick, skateboard, skateboard.getPrevSkaterPose(), priorPoseProgress);
        rotateForPose(livingEntity, model, poseStack, partialTick, skateboard, skateboard.getSkaterPose(), poseProgress);

    }


    public static boolean doesChangeModel(LivingEntity entity) {
        return entity.getVehicle() instanceof SkateboardEntity;
    }

    public static void saveModel(EntityModel model) {
        if (!models.stream().anyMatch(modelPositions -> modelPositions.isFor(model)) && !(model instanceof AdvancedEntityModel)) {
            models.add(new ModelPositions(model));
        }
    }

    public static void restoreModel(EntityModel model) {
        if (model instanceof AdvancedEntityModel) { // compat with my other mods that use the superior model format ;)
            ((AdvancedEntityModel) model).resetToDefaultPose();
            return;
        }
        Optional<ModelPositions> optional = models.stream().filter(modelPositions -> modelPositions.isFor(model)).findAny();
        if (optional.isPresent()) {
            optional.get().resetDefaultPositions();
        }
    }

    public static void setupSkaterAnimations(EntityModel model, LivingEntity livingEntity, SkateboardEntity skateboard, float partialTicks) {
        if (!modelsNeedingRestoration.contains(model)) {
            modelsNeedingRestoration.add(model);
        }
        float poseProgress = skateboard.getSkaterPoseProgress(partialTicks);
        float priorPoseProgress = 1F - poseProgress;
        animateForPose(model, livingEntity, partialTicks, skateboard, skateboard.getPrevSkaterPose(), priorPoseProgress);
        animateForPose(model, livingEntity, partialTicks, skateboard, skateboard.getSkaterPose(), poseProgress);
    }

    private static void animateForPose(EntityModel model, LivingEntity skater, float partialTicks, SkateboardEntity skateboard, SkaterPose pose, float progress) {
        ModelRootRegistry.SkateModelParts animationData = ModelRootRegistry.INSTANCE.getAnimationData(model, skater.getType());
        float animStrength = 1.0F;
        float animSpeed = 1.0F;
        boolean faceForwards = false;
        if(animationData != null){
            animStrength = animationData.strength();
            animSpeed = animationData.speed();
            faceForwards = animationData.faceForwards();
        }
        ModelPartWrapper[] body = ModelPartType.BODY.findIn(skater, model);
        ModelPartWrapper[] head = ModelPartType.HEAD.findIn(skater, model);
        ModelPartWrapper[] rightArm = ModelPartType.RIGHT_ARM.findIn(skater, model);
        ModelPartWrapper[] leftArm = ModelPartType.LEFT_ARM.findIn(skater, model);
        ModelPartWrapper[] rightLeg = ModelPartType.RIGHT_LEG.findIn(skater, model);
        ModelPartWrapper[] leftLeg = ModelPartType.LEFT_LEG.findIn(skater, model);
        float ageInTicks = skater.tickCount + partialTicks;
        float pedalAmount = skateboard.getPedalAmount(partialTicks);
        boolean pedalFootLeft = isPedalFootLeft(skater);
        boolean isBodyParentToLegs = Arrays.stream(body).anyMatch((ModelPartWrapper::isRoot));
        int leftMulti = pedalFootLeft ? -1 : 1;
        if(pose.isSideways() && !faceForwards){
            rotateModelPart(head, progress, 0, (float) Math.toRadians(-70 * leftMulti), 0);
        }
        if(skater.getType().is(IWSTags.HAS_ZOMBIE_ARMS)){
            rotateModelPart(leftArm, progress, (float) Math.toRadians(45F), 0, 0);
            rotateModelPart(rightArm, progress, (float) Math.toRadians(45F), 0, 0);
        }
        if(pose == SkaterPose.PEDAL){
            float swing = (float) Math.sin(ageInTicks * 0.4F * animSpeed) * pedalAmount * animStrength;
            positionModelPart(body, progress, 0, 0, 1);
            rotateModelPart(body, progress, (float) Math.toRadians(animStrength * 15F), 0, 0);
            if(isBodyParentToLegs){
                rotateModelPart(leftLeg, progress, (float) Math.toRadians(animStrength * -15F), 0, 0);
                rotateModelPart(rightLeg, progress, (float) Math.toRadians(animStrength * -15F), 0, 0);
            }
            rotateModelPart(leftArm, progress, (float) Math.toRadians(-35F - swing * 15) * pedalAmount, (float) Math.toRadians(-5F), (float) Math.toRadians(-10F));
            rotateModelPart(rightArm, progress, (float) Math.toRadians(-35F - swing * 15) * pedalAmount, (float) Math.toRadians(5F), (float) Math.toRadians(10F));
            if (pedalFootLeft) {
                if(!isBodyParentToLegs){
                    positionModelPart(leftLeg, progress, 0, -Math.abs(swing * 3), 1 + Math.abs(swing * 2));
                    positionModelPart(rightLeg, progress, 0, -3, 2);
                }
                rotateModelPart(leftLeg, progress, (float) Math.toRadians(15F - swing * 70), 0, 0);
                rotateModelPart(rightLeg, progress, (float) Math.toRadians(-10F), 0, 0);
            } else {
                if(!isBodyParentToLegs) {
                    positionModelPart(rightLeg, progress, 0, -Math.abs(swing * 3), 1 + Math.abs(swing * 2));
                    positionModelPart(leftLeg, progress, 0, -3, 2);
                }
                rotateModelPart(rightLeg, progress, (float) Math.toRadians(15F - swing * 70), 0, 0);
                rotateModelPart(leftLeg, progress, (float) Math.toRadians(-10F), 0, 0);
            }
        }else if(pose == SkaterPose.CROUCH){
            rotateModelPart(body, progress, (float) Math.toRadians(35F), 0, 0);
            rotateModelPart(head, progress, (float) Math.toRadians(-15F), 0, 0);
            rotateModelPart(rightLeg, progress, (float) Math.toRadians(-15F), 0, (float) Math.toRadians(25F));
            rotateModelPart(leftLeg, progress, (float) Math.toRadians(-15F), 0, (float) Math.toRadians(-25F));
            rotateModelPart(rightArm, progress, 0, (float) Math.toRadians(65F), (float) Math.toRadians(65F));
            rotateModelPart(leftArm, progress, 0, (float) Math.toRadians(-65F), (float) Math.toRadians(-65F));
            if(!isBodyParentToLegs) {
                positionModelPart(rightLeg, progress, 0, -3, 6);
                positionModelPart(leftLeg, progress, 0, -3, 6);
            }else{
                positionModelPart(body, progress, 0, 0, -6);
            }
        }else if(pose == SkaterPose.OLLIE){
            if(!isBodyParentToLegs) {
                positionModelPart(rightLeg, progress, 0, -3, 5);
                positionModelPart(leftLeg, progress, 0, -3, 5);
            }
            rotateModelPart(body, progress, (float) Math.toRadians(30), (float) Math.toRadians(-10), 0);
            rotateModelPart(head, progress, (float) Math.toRadians(-30), (float) Math.toRadians(-40), 0);
            if (pedalFootLeft) {
                rotateModelPart(leftLeg, progress, 0, 0, (float) Math.toRadians(-45F));
                rotateModelPart(rightLeg, progress, 0, 0, (float) Math.toRadians(-15));
                rotateModelPart(rightArm, progress, 0, (float) Math.toRadians(65F), (float) Math.toRadians(100F));
                rotateModelPart(leftArm, progress, 0, (float) Math.toRadians(-65F), (float) Math.toRadians(-115F));
            }else{
                rotateModelPart(leftLeg, progress, 0, 0, (float) Math.toRadians(15F));
                rotateModelPart(rightLeg, progress, 0, 0, (float) Math.toRadians(45F));
                rotateModelPart(rightArm, progress, 0, (float) Math.toRadians(65F), (float) Math.toRadians(115F));
                rotateModelPart(leftArm, progress, 0, (float) Math.toRadians(-65F), (float) Math.toRadians(-100F));
            }
        }else if(pose == SkaterPose.KICKFLIP){
            rotateModelPart(body, progress, (float) Math.toRadians(45), 0, 0);
            if (pedalFootLeft) {
                if(!isBodyParentToLegs) {
                    positionModelPart(rightLeg, progress, 0, -4, 8);
                    positionModelPart(leftLeg, progress, 0, -4, 4);
                }
                rotateModelPart(rightLeg, progress, (float) Math.toRadians(-35F), (float) Math.toRadians(5F), (float) Math.toRadians(35F));
                rotateModelPart(leftLeg, progress, (float) Math.toRadians(25F), 0, (float) Math.toRadians(-25F));
                rotateModelPart(leftArm, progress, 0, (float) Math.toRadians(-65F), (float) Math.toRadians(-100F));
                rotateModelPart(rightArm, progress, 0, (float) Math.toRadians(65F), (float) Math.toRadians(80F));
            }else{
                if(!isBodyParentToLegs) {
                    positionModelPart(rightLeg, progress, 0, -4, 4);
                    positionModelPart(leftLeg, progress, 0, -4, 8);
                }
                rotateModelPart(leftLeg, progress, (float) Math.toRadians(-35F), (float) Math.toRadians(-5F), (float) Math.toRadians(-35F));
                rotateModelPart(rightLeg, progress, (float) Math.toRadians(25F), 0, (float) Math.toRadians(25F));
                rotateModelPart(rightArm, progress, 0, (float) Math.toRadians(65F), (float) Math.toRadians(100F));
                rotateModelPart(leftArm, progress, 0, (float) Math.toRadians(-65F), (float) Math.toRadians(-80F));
            }
        }else if(pose == SkaterPose.GRIND){
            rotateModelPart(body, progress, (float) Math.toRadians(15), 0, 0);
            rotateModelPart(head, progress, (float) Math.toRadians(15), 0, 0);
            rotateModelPart(rightLeg, progress, (float) Math.toRadians(-25 * leftMulti), 0, 0);
            rotateModelPart(leftLeg, progress, (float) Math.toRadians(25 * leftMulti), 0, 0);
            if(!isBodyParentToLegs) {
                positionModelPart(rightLeg, progress, 0, -1, 2);
                positionModelPart(leftLeg, progress, 0, -1, 2);
            }
            if(pedalFootLeft){
                rotateModelPart(leftArm, progress, (float) Math.toRadians(-50), (float) Math.toRadians(-20), (float) Math.toRadians(-30F));
                rotateModelPart(rightArm, progress, (float) Math.toRadians(70), (float) Math.toRadians(-20), (float) Math.toRadians(30F));
            }else{
                rotateModelPart(rightArm, progress, (float) Math.toRadians(-50), (float) Math.toRadians(20), (float) Math.toRadians(30F));
                rotateModelPart(leftArm, progress, (float) Math.toRadians(70), (float) Math.toRadians(20), (float) Math.toRadians(-30F));
            }
        }
    }

    private static boolean isPedalFootLeft(LivingEntity skater) {
        return IWannaSkateMod.CLIENT_CONFIG.invertSide.get() == (skater.getMainArm() != HumanoidArm.LEFT);
    }

    private static void rotateForPose(LivingEntity skater, EntityModel model, PoseStack stack, float partialTicks, SkateboardEntity skateboard, SkaterPose pose, float progress) {
        ModelRootRegistry.SkateModelParts animationData = ModelRootRegistry.INSTANCE.getAnimationData(model, skater.getType());
        boolean pedalFootLeft = isPedalFootLeft(skater);
        int leftMulti = pedalFootLeft ? -1 : 1;
        if(pose.useBoardPitch()){
            float f = skateboard.getViewXRot(partialTicks) * progress;
            stack.mulPose(Vector3f.XN.rotationDegrees(f));
            stack.translate(0, -0.33F * (float) Math.sin(Math.toRadians(f)), 0);
        }
        if(pose.useBoardRoll()) {
            float yDif = Mth.wrapDegrees(skateboard.getViewYRot(partialTicks) - skater.yBodyRot);
            float moving = Mth.clamp(1 - (Math.abs(yDif) / 90F), 1, 1F);
            stack.mulPose(Vector3f.ZN.rotationDegrees(skateboard.getZRot(partialTicks) * progress * moving));
        }
        if(pose.isSideways() && (animationData == null || !animationData.faceForwards())){
            stack.mulPose(Vector3f.YP.rotationDegrees(-90 * progress * leftMulti));
        }
        if(pose == SkaterPose.PEDAL && progress > 0.0F){
            float up = (float) Math.sin(Math.toRadians(skateboard.getViewXRot(partialTicks))) * skateboard.getOnGroundProgress(partialTicks);
            stack.translate((0.2F + 0.05F * (1 - up)) * progress * leftMulti, (-0.2F + up * 0.1F) * progress , (-0.1F + up * -0.6F) * progress);
        }else if(pose == SkaterPose.CROUCH){
            stack.translate(0, -0.25F * progress, -0.15F * progress * leftMulti);
        }else if(pose == SkaterPose.OLLIE){
            stack.translate(-0.4F * progress, 0, -0.35F * progress * leftMulti);
            stack.mulPose(Vector3f.ZN.rotationDegrees(20 * progress));
        }else if(pose == SkaterPose.KICKFLIP){
            stack.translate(0.1F * progress, -0.4F * progress, -0.1F * progress * leftMulti);
            stack.mulPose(Vector3f.XN.rotationDegrees(10 * progress));
        }else if(pose == SkaterPose.GRIND){
            stack.translate(0, -0.2F * progress, -0.2F * progress * leftMulti);
            stack.mulPose(Vector3f.XN.rotationDegrees(-15 * progress));
        }
    }

    private static void rotateModelPart(ModelPartWrapper[] parts, float progress, float x, float y, float z) {
        for(ModelPartWrapper model : parts){
            ModelPart part = model.getModelPart();
            AdvancedModelBox box = model.getAdvancedModelBox();
            if(part != null) {
                part.xRot += progress * x;
                part.yRot += progress * y;
                part.zRot += progress * z;
            }
            if(box != null) {
                box.rotateAngleX += progress * x;
                box.rotateAngleY += progress * y;
                box.rotateAngleZ += progress * z;
            }
        }
    }

    private static void positionModelPart(ModelPartWrapper[] parts, float progress, float x, float y, float z) {
        for(ModelPartWrapper model : parts){
            ModelPart part = model.getModelPart();
            AdvancedModelBox box = model.getAdvancedModelBox();
            if(part != null){
                part.x += progress * x;
                part.y += progress * y;
                part.z += progress * z;
            }
            if(box != null){
                box.rotationPointX += progress * x;
                box.rotationPointY += progress * y;
                box.rotationPointZ += progress * z;
            }
        }
    }
}
