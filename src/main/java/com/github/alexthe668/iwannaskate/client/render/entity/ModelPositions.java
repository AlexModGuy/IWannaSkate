package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

import java.lang.reflect.Field;
import java.util.*;

public class ModelPositions {

    private EntityModel entityModel;
    private Map<ModelPart, PartPose> positionMap = new HashMap<>();

    public ModelPositions(EntityModel entityModel) {
        this.entityModel = entityModel;
        this.updateDefaultPositions();
    }

    //helper function to gather model parts from superclasses
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public void updateDefaultPositions() {
        List<Field> fields = getAllFields(new LinkedList<>(), entityModel.getClass());

        try {
            for (Field f : fields) {
                f.setAccessible(true);
                if ( f.get(entityModel) instanceof ModelPart part) {
                    positionMap.put(part, PartPose.offsetAndRotation(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot));
                }
            }
        } catch (Exception e) {
            IWannaSkateMod.LOGGER.warn("could not save default position of model[" + entityModel.getClass().getSimpleName() + "] for animating on a skateboard.");
            e.printStackTrace();
        }
    }

    public void resetDefaultPositions(){
        positionMap.forEach(((modelPart, modelPosition) -> updatePart(modelPart, modelPosition)));
    }

    public boolean isFor(EntityModel model){
        return this.entityModel == model;
    }

    private void updatePart(ModelPart modelPart, PartPose modelPosition) {
        modelPart.x = modelPosition.x;
        modelPart.y = modelPosition.y;
        modelPart.z = modelPosition.z;
        modelPart.xRot = modelPosition.xRot;
        modelPart.yRot = modelPosition.yRot;
        modelPart.zRot = modelPosition.zRot;
    }

}
