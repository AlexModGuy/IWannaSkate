package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import net.minecraft.client.model.geom.ModelPart;

public class ModelPartWrapper {
    private ModelPart modelPart;
    private AdvancedModelBox advancedModelBox;

    public ModelPartWrapper(){
        this.modelPart = null;
        this.advancedModelBox = null;
    }

    public ModelPartWrapper(ModelPart part){
        this.modelPart = part;
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
}
