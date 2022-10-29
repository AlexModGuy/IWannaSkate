package com.github.alexthe668.iwannaskate.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SkaterCapModel extends HumanoidModel {

    public SkaterCapModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");
        float angle = (float)Math.toRadians(-3F);
        head.addOrReplaceChild("head", CubeListBuilder.create().texOffs(19, 0).addBox(-4.0F, -3.0F, 2.0F, 8.0F, 0.0F, 6.0F, deformation), PartPose.offsetAndRotation(0.0F, -0.15F, 1.75F, angle, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

}