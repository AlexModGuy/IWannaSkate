package com.github.alexthe668.iwannaskate.client.sound;

import com.github.alexthe668.iwannaskate.client.ClientProxy;
import com.github.alexthe668.iwannaskate.server.entity.SkateboardEntity;
import com.github.alexthe668.iwannaskate.server.misc.IWSSoundRegistry;
import com.github.alexthe668.iwannaskate.server.misc.SkateQuality;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SkateboardSound extends AbstractTickableSoundInstance {
    protected final SkateboardEntity skateboard;
    private SkateSoundType soundType;
    private float changeProgress;

    public SkateboardSound(SkateSoundType soundType, float startingVolume, SkateboardEntity skateboard) {
        super(soundType.sound.get(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.soundType = soundType;
        this.skateboard = skateboard;
        this.looping = true;
        this.delay = 0;
        this.volume = startingVolume;
        this.changeProgress = 0;
        this.x = this.skateboard.getX();
        this.y = this.skateboard.getY();
        this.z = this.skateboard.getZ();
    }

    public boolean canPlaySound() {
        return !this.skateboard.isSilent() && this.skateboard.canWheelsMakeSound() && (ClientProxy.SKATEBOARD_SOUND_MAP.get(this.skateboard.getId()) == this || changeProgress < 1.0F);
    }

    public boolean canStartSilent() {
        return true;
    }

    public void tick() {
        boolean flag = this.shouldSwitchSounds();
        if (flag) {
            if(this.changeProgress >= 1.0F){
                this.stop();
            }
            if(!ClientProxy.SKATEBOARD_SOUND_MAP.containsKey(skateboard.getId()) || ClientProxy.SKATEBOARD_SOUND_MAP.get(skateboard.getId()).soundType != SkateSoundType.getForSkateboard(skateboard)){
                ClientProxy.SKATEBOARD_SOUND_MAP.put(skateboard.getId(), this.createNewSoundFrom());
            }
        }
        if (!this.skateboard.isRemoved() && this.skateboard.isAlive()) {
            this.pitch = 1.0F;
            this.x = this.skateboard.getX();
            this.y = this.skateboard.getY();
            this.z = this.skateboard.getZ();
            this.volume = getTargetVolume() * Math.max(0, 1 - changeProgress);
            if(flag){
                changeProgress += 0.5F;
            }
        } else {
            this.stop();
            ClientProxy.SKATEBOARD_SOUND_MAP.remove(skateboard.getId());
        }
    }

    private float getTargetVolume(){
        float f = (float)this.skateboard.getDeltaMovement().horizontalDistance();
        float volumeLerp = this.skateboard.getOnGroundProgress(1.0F);
        if(this.skateboard.isGrinding()){
            volumeLerp = 1F;
        }
        return Mth.lerp(Mth.clamp((float)Math.pow(f, 0.5F) * volumeLerp, 0.0F, 1F), 0.0F, 1F);

    }

    private SkateboardSound createNewSoundFrom(){
        return new SkateboardSound(SkateSoundType.getForSkateboard(skateboard), 0, skateboard);
    }

    private boolean shouldSwitchSounds() {
        return soundType != SkateSoundType.getForSkateboard(skateboard);
    }

    public boolean isDifferentBoard(Entity entity) {
        return this.skateboard != entity;
    }
}
