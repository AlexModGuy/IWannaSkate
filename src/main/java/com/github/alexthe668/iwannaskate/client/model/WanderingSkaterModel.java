package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.AbstractVillager;

public class WanderingSkaterModel extends HierarchicalModel<WanderingSkaterEntity> implements HeadedModel, VillagerHeadModel {

    private final ModelPart head;
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart right_leg;
    private final ModelPart left_leg;

    public WanderingSkaterModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.arms = root.getChild("arms");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition headwear = head.addOrReplaceChild("headwear", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition headwear2 = head.addOrReplaceChild("headwear2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rotation = headwear2.addOrReplaceChild("rotation", CubeListBuilder.create().texOffs(0, 36).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 5.0F, -1.5708F, 0.0F, 0.0F));
        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition arms = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 3.5F, 0.3F));
        PartDefinition arms_rotation = arms.addOrReplaceChild("arms_rotation", CubeListBuilder.create().texOffs(16, 36).addBox(-8.0F, 0.0F, -1.05F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(32, 31).addBox(-4.0F, 4.0F, -1.05F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.05F, -0.7505F, 0.0F, 0.0F));
        PartDefinition arms_flipped = arms_rotation.addOrReplaceChild("arms_flipped", CubeListBuilder.create().texOffs(16, 36).mirror().addBox(4.0F, -24.0F, -1.05F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 16).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 16).mirror().addBox(-2.0F, 1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    public void setupAnim(WanderingSkaterEntity skater, float limbSwing, float limbSwingAmount, float ageInTicks, float rotY, float rotX) {
        float partialTick = ageInTicks - skater.tickCount;
        float attack = skater.getAttackingProgress(partialTick);
        this.root.yRot = (float) (attack * Math.toRadians(Mth.wrapDegrees(skater.getAttackingAngle(partialTick))));
        limbSwing = limbSwing + (attack * ageInTicks);
        limbSwingAmount = Math.max(attack, limbSwingAmount);
        boolean flag = skater.getUnhappyCounter() > 0;

        this.head.yRot = rotY * ((float)Math.PI / 180F);
        this.head.xRot = rotX * ((float)Math.PI / 180F);
        if (flag) {
            this.head.zRot = 0.3F * Mth.sin(0.45F * ageInTicks);
            this.head.xRot = 0.4F;
        } else {
            this.head.zRot = 0.0F;
        }

        this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.right_leg.yRot = 0.0F;
        this.left_leg.yRot = 0.0F;
    }

    public void translateToArms(PoseStack stack) {
        this.root.translateAndRotate(stack);
        this.body.translateAndRotate(stack);
    }

    @Override
    public ModelPart getHead() {
        return head;
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void hatVisible(boolean p_104035_) {

    }
}
