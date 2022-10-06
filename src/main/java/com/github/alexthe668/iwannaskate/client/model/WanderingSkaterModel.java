package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;

public class WanderingSkaterModel extends VillagerModel<WanderingSkaterEntity> {

    protected final ModelPart nose;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart hatRim;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public WanderingSkaterModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hatRim = this.hat.getChild("hat_rim");
        this.nose = this.head.getChild("nose");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createBodyModel(), 64, 64);
    }

    public void setupAnim(WanderingSkaterEntity skater, float limbSwing, float limbSwingAmount, float ageInTicks, float rotX, float rotY) {
        float partialTick = ageInTicks - skater.tickCount;
        float attack = skater.getAttackingProgress(partialTick);
        this.root.yRot = (float) (attack * Math.toRadians(Mth.wrapDegrees(skater.getAttackingAngle(partialTick))));
        limbSwing = limbSwing + (attack * ageInTicks);
        limbSwingAmount = Math.max(attack, limbSwingAmount);
        super.setupAnim(skater, limbSwing, limbSwingAmount, ageInTicks, rotX, rotY);
    }

    public void translateToArms(PoseStack stack){
        this.root.translateAndRotate(stack);
    }
}
