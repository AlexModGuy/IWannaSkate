package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.server.entity.HasAnimationFlags;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerModel.class)
public class VillagerModelMixin {

    @Shadow @Final protected ModelPart nose;

    @Shadow @Final private ModelPart head;

    @Inject(
            method = {"Lnet/minecraft/client/model/VillagerModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void iws_setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotY, float rotX, CallbackInfo ci) {
        float noseRotX = 0.0F;
        if(entity instanceof HasAnimationFlags animatedEntity && animatedEntity.getIWSAnimationFlags() == 1){
            noseRotX = -0.4F;
            this.head.yRot = 0;
            this.head.xRot = -0.3F;
        }
        this.nose.xRot = noseRotX;
    }

}
