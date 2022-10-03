package com.github.alexthe668.iwannaskate.client.sound;

import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.misc.IWSSoundRegistry;
import com.github.alexthe668.iwannaskate.server.misc.SkateQuality;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public enum SkateSoundType {
    SMOOTH(IWSSoundRegistry.SKATEBOARD_SMOOTH_ROLLING_LOOP),
    ROUGH(IWSSoundRegistry.SKATEBOARD_ROUGH_ROLLING_LOOP),
    GRIND(IWSSoundRegistry.SKATEBOARD_GRIND_LOOP);

    RegistryObject<SoundEvent> sound;

    SkateSoundType(RegistryObject<SoundEvent> sound) {
        this.sound = sound;
    }

    public static SkateSoundType getForSkateboard(SkateboardEntity skateboard){
        if(skateboard.isGrinding()){
            return GRIND;
        }else if(skateboard.skateQuality.ordinal() <= SkateQuality.MEDIUM.ordinal()){
            return ROUGH;
        }else{
            return SMOOTH;
        }
    }
}
