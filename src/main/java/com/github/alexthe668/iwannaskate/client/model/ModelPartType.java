package com.github.alexthe668.iwannaskate.client.model;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.common.Mod;

public enum ModelPartType {
    BODY,
    HEAD,
    LEFT_ARM,
    RIGHT_ARM,
    LEFT_LEG,
    RIGHT_LEG;


    private static final ModelPartWrapper[] EMPTY = new ModelPartWrapper[]{new ModelPartWrapper()};

    public ModelPartWrapper[] findIn(Entity entity, EntityModel model) {
        ModelRootRegistry.SkateModelParts partsFromJson = ModelRootRegistry.INSTANCE.getAnimationData(model, entity.getType());
        if(partsFromJson != null){
            switch (this){
                case BODY:
                    return partsFromJson.body() != null ? partsFromJson.body() : EMPTY;
                case HEAD:
                    return partsFromJson.head() != null ? partsFromJson.head() : EMPTY;
                case LEFT_LEG:
                    return partsFromJson.leftLeg() != null ? partsFromJson.leftLeg() : EMPTY;
                case RIGHT_LEG:
                    return partsFromJson.rightLeg() != null ? partsFromJson.rightLeg() : EMPTY;
                case LEFT_ARM:
                    return partsFromJson.leftArm() != null ? partsFromJson.leftArm() : EMPTY;
                case RIGHT_ARM:
                    return partsFromJson.rightArm() != null ? partsFromJson.rightArm() : EMPTY;
            }
        }
        return model instanceof HumanoidModel ? findInDefaultHumanoid((HumanoidModel) model) : EMPTY;
    }

    private ModelPartWrapper[] findInDefaultHumanoid(HumanoidModel model){
        switch (this){
            case BODY:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.body)};
            case HEAD:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.head)};
            case LEFT_LEG:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.leftLeg)};
            case RIGHT_LEG:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.rightLeg)};
            case LEFT_ARM:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.leftArm)};
            case RIGHT_ARM:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.rightArm)};
        }
        return EMPTY;
    }
}
