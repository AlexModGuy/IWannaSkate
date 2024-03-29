package com.github.alexthe668.iwannaskate.client.model;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class IWSModelLayers {

    public static final ModelLayerLocation SKATER_SKELETON = createLocation("skater_skeleton", "main");
    public static final ModelLayerLocation WANDERING_SKATER = createLocation("wandering_skater", "main");
    public static final ModelLayerLocation SPIKED_SKATER_HELMET = createLocation("spiked_skater_helmet", "main");
    public static final ModelLayerLocation BEANIE = createLocation("beanie", "main");

    public static final ModelLayerLocation SKATER_CAP = createLocation("skater_cap", "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SKATER_SKELETON, () -> SkeletonModel.createBodyLayer());
        event.registerLayerDefinition(WANDERING_SKATER, () -> WanderingSkaterModel.createBodyLayer());
        event.registerLayerDefinition(SPIKED_SKATER_HELMET, () -> SpikedSkaterHelmetModel.createArmorLayer(new CubeDeformation(1.0F)));
        event.registerLayerDefinition(BEANIE, () -> BeanieModel.createArmorLayer(new CubeDeformation(1.0F)));
        event.registerLayerDefinition(SKATER_CAP, () -> SkaterCapModel.createArmorLayer(new CubeDeformation(1.0F)));
    }

    private static ModelLayerLocation createLocation(String model, String layer) {
        return new ModelLayerLocation(new ResourceLocation(IWannaSkateMod.MODID, model), layer);
    }


}
