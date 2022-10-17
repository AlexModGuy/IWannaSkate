package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class PlayerCapes {
    private static final ResourceLocation DEV_CAPE_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/dev_cape.png");
    private static final ResourceLocation BETA_TESTER_CAPE_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/beta_tester_cape.png");
    private static final List<UUID> DEVS = Lists.newArrayList(
            UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c"), //Alexthe666
            UUID.fromString("15be46af-ab50-4d04-acaf-bbf713faf1f9") //crydigo
    );
    private static final List<UUID> BETA_TESTERS = Lists.newArrayList(
            UUID.fromString("84a4c50d-8517-4742-8e90-6800537c73ce"), //Unknion
            UUID.fromString("ddec1452-118b-4e40-a51b-2ef150343fc7"), //AquaticFlapper
            UUID.fromString("97399daf-aecd-45c9-a6f2-c18e9c9b18a2"), //PlatypusOfHats
            UUID.fromString("0ca35240-695b-4f24-a37b-f48e7354b6fc"), //Ron0
            UUID.fromString("77ff4bbf-4316-43e5-96f2-552e855449e4"), //Crackercraft
            UUID.fromString("8c1af44c-d02a-42e8-8ae6-e3f2132acbbf"), //Plummet_Studios
            UUID.fromString("57699de4-ee48-4a8a-9b0e-e283175f4599"), //_FallenReaper
            UUID.fromString("d80544ec-c9e0-4b66-9c79-bf61305b32ea"), //BrayDoesStuff
            UUID.fromString("d80544ec-c9e0-4b66-9c79-bf61305b32ea"), //BrayDoesStuff
            UUID.fromString("59751bb1-f3d9-40ad-b72f-2f0f628e9263"), //Anthony5150
            UUID.fromString("54f5ea9b-1713-4881-a545-66342b3fc70c"), //Weenus08
            UUID.fromString("4612ba26-8a8e-4586-9fb3-21b36b921190"), //XVtheawesome4
            UUID.fromString("59f42128-739f-42ce-b2c4-745f7fdd9a89"), //Frenzymoss3,
            UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290") //Carro1001
    );


    public static void registerTexturesFor(PlayerInfo playerInfo) {
        if(playerInfo.textureLocations.get(MinecraftProfileTexture.Type.CAPE) == null){
            if(DEVS.contains(playerInfo.getProfile().getId())){
                playerInfo.textureLocations.put(MinecraftProfileTexture.Type.CAPE, PlayerCapes.DEV_CAPE_TEXTURE);
                playerInfo.textureLocations.put(MinecraftProfileTexture.Type.ELYTRA, PlayerCapes.DEV_CAPE_TEXTURE);
            }else if(BETA_TESTERS.contains(playerInfo.getProfile().getId())){
                playerInfo.textureLocations.put(MinecraftProfileTexture.Type.CAPE, PlayerCapes.BETA_TESTER_CAPE_TEXTURE);
                playerInfo.textureLocations.put(MinecraftProfileTexture.Type.ELYTRA, PlayerCapes.BETA_TESTER_CAPE_TEXTURE);
            }
        }
    }

}
