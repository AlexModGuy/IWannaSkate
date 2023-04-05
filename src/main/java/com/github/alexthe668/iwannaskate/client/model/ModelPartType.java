package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public enum ModelPartType {
    BODY,
    HEAD,
    LEFT_ARM,
    RIGHT_ARM,
    LEFT_LEG,
    RIGHT_LEG;


    private static final ModelPartWrapper[] EMPTY = new ModelPartWrapper[]{new ModelPartWrapper()};
    private static Map<EntityModel, ModelPartWrapper[]> DEFAULT_HUMAN_MAPPINGS = new HashMap<>();


    public ModelPartWrapper[] findIn(Entity entity, EntityModel model) {
        if(IWannaSkateMod.CLIENT_CONFIG.animateAllEntityModels.get()){
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
        }
        if(false){
            return DEFAULT_HUMAN_MAPPINGS.get(model);
        }else{
            ModelPartWrapper[] mappings;
            if(model instanceof PlayerModel){
                mappings = findInDefaultPlayer((PlayerModel) model);
            }else if(model instanceof HumanoidModel){
                mappings = findInDefaultHumanoid((HumanoidModel) model);
            }else{
                mappings = EMPTY;
            }
            DEFAULT_HUMAN_MAPPINGS.put(model, mappings);
            return mappings;
        }
    }

    private ModelPartWrapper[] findInDefaultHumanoid(HumanoidModel model){
        switch (this){
            case BODY:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.body)};
            case HEAD:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.head), new ModelPartWrapper(model.hat)};
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

    private ModelPartWrapper[] findInDefaultPlayer(PlayerModel model){
        switch (this){
            case BODY:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.body), new ModelPartWrapper(model.jacket)};
            case HEAD:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.head), new ModelPartWrapper(model.hat)};
            case LEFT_LEG:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.leftLeg), new ModelPartWrapper(model.leftPants)};
            case RIGHT_LEG:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.rightLeg), new ModelPartWrapper(model.rightPants)};
            case LEFT_ARM:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.leftArm), new ModelPartWrapper(model.leftSleeve)};
            case RIGHT_ARM:
                return new ModelPartWrapper[]{new ModelPartWrapper(model.rightArm), new ModelPartWrapper(model.rightSleeve)};
        }
        return EMPTY;
    }
}
