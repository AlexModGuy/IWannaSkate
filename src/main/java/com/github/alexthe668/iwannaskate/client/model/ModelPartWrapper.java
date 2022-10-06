package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import net.minecraft.client.model.geom.ModelPart;

public class ModelPartWrapper {
    private ModelPart modelPart;
    private AdvancedModelBox advancedModelBox;
    private boolean isRoot;

    public ModelPartWrapper(){
        this.modelPart = null;
        this.advancedModelBox = null;
        this.isRoot = false;
    }

    public ModelPartWrapper(ModelPart part){
        this(part, false);
    }

    public ModelPartWrapper(ModelPart part, boolean isRoot){
        this.modelPart = part;
        this.isRoot = isRoot;
    }

    public ModelPartWrapper(AdvancedModelBox part){
        this.advancedModelBox = part;
    }

    public ModelPart getModelPart() {
        return modelPart;
    }

    public AdvancedModelBox getAdvancedModelBox() {
        return advancedModelBox;
    }

    public boolean isRoot(){
        return isRoot;
    }

}
