package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.client.render.entity.SkatingModelPositioner;
import com.github.alexthe668.iwannaskate.server.misc.PlayerCapes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin {

    @Inject(
            method = {"Lnet/minecraft/client/multiplayer/PlayerInfo;registerTextures()V"},
            remap = true,
            at = @At(value = "RETURN")
    )
    protected void iws_registerTextures(CallbackInfo ci) {
        PlayerCapes.registerTexturesFor((PlayerInfo)(Object)this);
    }
}
