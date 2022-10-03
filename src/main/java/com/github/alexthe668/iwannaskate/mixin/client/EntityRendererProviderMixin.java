package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.client.model.ModelRootRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRendererProvider.Context.class)
public class EntityRendererProviderMixin {

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;bakeLayer(Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/model/geom/ModelPart;"},
            remap = true,
            at = @At(value = "TAIL"),
            cancellable = true
    )
    protected void iws_bakeLayer(ModelLayerLocation modelLayerLocation, CallbackInfoReturnable<ModelPart> cir){
        ModelRootRegistry.onCallBakeLayer(modelLayerLocation, cir.getReturnValue());
    }
}
