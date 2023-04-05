package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.Entity;

public class HoverParticleModel extends AdvancedEntityModel {
    private final AdvancedModelBox box;

    public HoverParticleModel() {
        texWidth = 16;
        texHeight = 16;

        box = new AdvancedModelBox(this);
        box.setRotationPoint(0.0F, 24.0F, 0.0F);
        box.setTextureOffset(0, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(box);
    }

    @Override
    public void setupAnim(Entity entity, float v, float v1, float v2, float v3, float v4) {
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(box);
    }

}