package com.github.alexthe668.iwannaskate.mixin;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.SlowableEntity;
import com.github.alexthe668.iwannaskate.server.entity.SpecialSlowMotion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(
            method = {"Lnet/minecraft/server/level/ServerLevel;tickNonPassenger(Lnet/minecraft/world/entity/Entity;)V"},
            remap = true,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void iws_tickNonPassenger(Entity entity, CallbackInfo ci) {
        if(entity instanceof SlowableEntity slowableEntity && slowableEntity.cancelTick(server.getTickCount()) && IWannaSkateMod.COMMON_CONFIG.enableSlowMotion.get()){
            entity.setOldPosAndRot();
            ci.cancel();
        }
        if(entity instanceof SpecialSlowMotion specialSlowMotion){
            specialSlowMotion.onMasterServerTick();
        }
    }
}
