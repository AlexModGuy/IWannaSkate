package com.github.alexthe668.iwannaskate.client.render.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.client.color.BoardColorSampler;
import com.github.alexthe668.iwannaskate.client.color.DeckTexture;
import com.github.alexthe668.iwannaskate.client.model.SkateboardModel;
import com.github.alexthe668.iwannaskate.client.render.IWSRenderTypes;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkateboardTexturer {

    private static final ResourceLocation[] RAW_DECK_TEXTURES = new ResourceLocation[]{
            new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/deck/deck_0.png"),
            new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/deck/deck_1.png"),
            new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/deck/deck_2.png"),
            new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/deck/deck_3.png")
    };
    private static final Map<DyeColor, ResourceLocation> GRIP_TAPE_TEXTURES = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        for (DyeColor dyeColor : DyeColor.values()) {
            map.put(dyeColor, new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/grip_tape/grip_tape_" + dyeColor.getName() + ".png"));
        }
    });

    private static final ResourceLocation BASE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/base.png");
    private static final Map<Holder<BannerPattern>, ResourceLocation> BANNER_PATTERN_RESOURCE_LOCATION_HASH_MAP = new HashMap<>();
    private static final ResourceLocation SPOOKY_GLOW_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/wheels/wheels_spooky_glow.png");
    private static final ResourceLocation HOVER_GLOW_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/wheels/wheels_hover_glow.png");
    private static final Map<ResourceLocation, ResourceLocation> DECK_TEXTURES_FOR_BLOCK = new HashMap<>();

    private static final SkateboardModel GRIPTAPE_MODEL = new SkateboardModel();

    private static final SkateboardModel TRUCKS_MODEL = new SkateboardModel();
    private static final SkateboardModel BANNER_MODEL = new SkateboardModel();
    private static final SkateboardModel WHEELS_MODEL = new SkateboardModel();

    public static void renderDeck(SkateboardModel model, SkateboardData data, PoseStack stack, MultiBufferSource source, int packedLight, boolean glint) {
        ResourceLocation deckTexture;
        if(DECK_TEXTURES_FOR_BLOCK.containsKey(data.getWoodBlock())){
            deckTexture = DECK_TEXTURES_FOR_BLOCK.get(data.getWoodBlock());
        }else{
            ResourceLocation res = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/generated/deck_" + data.getWoodBlock().getNamespace() + "_" + data.getWoodBlock().getPath());
            int[] colors = BoardColorSampler.getColor(data.getWoodBlock());
            deckTexture = DeckTexture.getOrCreateDeckTexture(res, RAW_DECK_TEXTURES, colors);
            DECK_TEXTURES_FOR_BLOCK.put(data.getWoodBlock(), deckTexture);
        }
        model.hideWheels();
        model.renderToBuffer(stack, getVertexConsumer(source, RenderType.entitySolid(deckTexture), glint), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        model.showWheels();
    }

    public static void renderBoard(SkateboardModel model, SkateboardData data, PoseStack stack, MultiBufferSource source, int packedLight, boolean glint) {
        renderDeck(model, data, stack, source, packedLight, glint);
        if(data.getWheelType().hideTrucks()){
            TRUCKS_MODEL.hideWheels();
        }
        TRUCKS_MODEL.copyFrom(model);
        TRUCKS_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.entityCutoutNoCull(BASE), false), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if(data.getWheelType().hideTrucks()){
            TRUCKS_MODEL.showWheels();
        }
        if (data.hasBanner()) {
            BANNER_MODEL.copyFrom(model);
            List<Pair<Holder<BannerPattern>, DyeColor>> list = BannerBlockEntity.createPatterns(getBannerColor(data), getItemPatterns(data));
            for (int i = 0; i < 17 && i < list.size(); ++i) {
                Pair<Holder<BannerPattern>, DyeColor> pair = list.get(i);
                float[] rgb = pair.getSecond().getTextureDiffuseColors();
                Holder<BannerPattern> pattern = pair.getFirst();
                ResourceLocation patternTexture;
                if (BANNER_PATTERN_RESOURCE_LOCATION_HASH_MAP.containsKey(pattern)) {
                    patternTexture = BANNER_PATTERN_RESOURCE_LOCATION_HASH_MAP.get(pattern);
                } else {
                    patternTexture = generatePatternTexture(pattern);
                    BANNER_PATTERN_RESOURCE_LOCATION_HASH_MAP.put(pattern, patternTexture);
                }
                BANNER_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.entityNoOutline(patternTexture), false), packedLight, OverlayTexture.NO_OVERLAY, rgb[0], rgb[1], rgb[2], 1.0F);
            }
        }

        if (data.hasGripTape()) {
            GRIPTAPE_MODEL.copyFrom(model);
            GRIPTAPE_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.entityTranslucent(GRIP_TAPE_TEXTURES.get(data.getGripTapeColor())), glint), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        SkateboardWheels wheelType = data.getWheelType();
        WHEELS_MODEL.copyFrom(model);
        if(wheelType.isEmissive()){
            WHEELS_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.entityTranslucentEmissive(data.getWheelType().getTexture()), false), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }else{
            WHEELS_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.entityCutoutNoCull(data.getWheelType().getTexture()), false), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if(wheelType == SkateboardWheels.SPOOKY){
            WHEELS_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.eyes(SPOOKY_GLOW_TEXTURE), false), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if(wheelType == SkateboardWheels.HOVER){
            WHEELS_MODEL.renderToBuffer(stack, getVertexConsumer(source, RenderType.eyes(HOVER_GLOW_TEXTURE), false), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static ResourceLocation generatePatternTexture(Holder<BannerPattern> pattern) {
        ResourceLocation res = pattern.unwrapKey().get().location();
        return new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/banner/" + res.getNamespace() + "/" + res.getPath() + ".png");
    }

    @Nullable
    private static DyeColor getBannerColor(SkateboardData data) {
        CompoundTag compoundtag = data.getBannerTag();
        return compoundtag != null ? DyeColor.byId(compoundtag.getInt("Base")) : DyeColor.WHITE;
    }

    @Nullable
    private static ListTag getItemPatterns(SkateboardData data) {
        ListTag listtag = null;
        CompoundTag compoundtag = data.getBannerTag();
        if (compoundtag != null && compoundtag.contains("Patterns", 9)) {
            listtag = compoundtag.getList("Patterns", 10).copy();
        }
        return listtag;
    }


    public static VertexConsumer getVertexConsumer(MultiBufferSource bufferSource, RenderType renderType, boolean glint) {
        return glint ? VertexMultiConsumer.create(bufferSource.getBuffer(IWSRenderTypes.SKATEBOARD_GLINT), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
    }

}
