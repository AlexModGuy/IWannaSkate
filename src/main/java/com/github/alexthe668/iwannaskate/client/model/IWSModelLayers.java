package com.github.alexthe668.iwannaskate.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class IWSModelLayers {

    public static final ModelLayerLocation SKATER_SKELETON = createLocation("skater_skeleton", "main");
    public static final ModelLayerLocation WANDERING_SKATER = createLocation("wandering_skater", "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SKATER_SKELETON, () -> SkeletonModel.createBodyLayer());
        event.registerLayerDefinition(WANDERING_SKATER, () -> LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64));
    }

    private static ModelLayerLocation createLocation(String model, String layer) {
        return new ModelLayerLocation(new ResourceLocation("alexsmobs", model), layer);
    }


}
