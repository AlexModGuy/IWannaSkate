package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.client.model.HasHeadModelParts;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AgeableListModel.class)
public abstract class AgeableListModelMixin implements HasHeadModelParts {

    @Shadow protected abstract Iterable<ModelPart> headParts();

    @Override
    public Iterable<ModelPart> getHeadModelParts() {
        return headParts();
    }
}
