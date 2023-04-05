package com.github.alexthe668.iwannaskate.mixin.client;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.network.SkateboardJumpMessage;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {


    @Shadow public abstract float getJumpRidingScale();

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(
            method = {"Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_jumpableVehicle(CallbackInfoReturnable<PlayerRideableJumping> cir){
        if(getVehicle() instanceof SkateboardEntity skateboard && skateboard.canJump()){
            cir.setReturnValue(skateboard);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/player/LocalPlayer;sendRidingJump()V"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_sendJump(CallbackInfo ci){
        if(getVehicle() instanceof SkateboardEntity skateboard){
            IWannaSkateMod.sendMSGToServer(new SkateboardJumpMessage(skateboard.getId(), this.getId(), Mth.floor(this.getJumpRidingScale() * 100.0F)));
            ci.cancel();
        }
    }
}
