package com.github.alexthe668.iwannaskate.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SpikedSkaterHelmetModel extends HumanoidModel {

    public SpikedSkaterHelmetModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createArmorLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(deformation, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.getChild("head");
        CubeDeformation deformation1 = new CubeDeformation(0.25F);
        head.addOrReplaceChild("spikes", CubeListBuilder.create().texOffs(0, 0)
                .texOffs(24, 3).addBox(-1.0F, -11.0F, -3.3F, 2.0F, 2.0F, 3.0F, deformation1)
                .texOffs(32, 5).addBox(0.0F, -10.0F, 1.6F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

}