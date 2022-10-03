package com.github.alexthe668.iwannaskate.server;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.SkateboardMaterials;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IWannaSkateMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public void init() {
    }

    public void clientInit() {
    }

    public Object getISTERProperties() {
        return null;
    }


    @SubscribeEvent
    public void onTagsLoaded(TagsUpdatedEvent event){
        SkateboardMaterials.reload();
    }

    public void setHoverItem(ItemStack stack){}

    public Player getClientSidePlayer() {
        return null;
    }

    public boolean isKeyDown(int keyType) {
        return false;
    }

    public void onEntityStatus(Entity entity, byte updateKind) {
    }
}
