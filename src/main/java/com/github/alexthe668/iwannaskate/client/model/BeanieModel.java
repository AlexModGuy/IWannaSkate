package com.github.alexthe668.iwannaskate.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BeanieModel extends HumanoidModel {

    public BeanieModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");
        CubeDeformation deformation1 = new CubeDeformation(0.35F);
        float angle = (float)Math.toRadians(-15F);
        head.addOrReplaceChild("beanie", CubeListBuilder.create().texOffs(0, 0)
                        .texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation1), PartPose.offsetAndRotation(0.0F, -1F, -1.0F, angle, 0.0F, 0.0F));
        head.addOrReplaceChild("beanie_rim", CubeListBuilder.create().texOffs(0, 0)
                .texOffs(32, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation1.extend(0.5F)), PartPose.offsetAndRotation(0.0F, -1F, -1.0F, angle, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

}