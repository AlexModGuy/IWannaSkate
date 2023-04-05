package com.github.alexthe668.iwannaskate.client.render;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class IWSRenderTypes extends RenderType {

    public static final ResourceLocation SKATEBOARD_GLINT_TEXTURE = new ResourceLocation(IWannaSkateMod.MODID, "textures/entity/skateboard/enchanted_glint.png");

    private IWSRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }


    protected static final RenderStateShard.TexturingStateShard SKATE_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("skate_glint_texturing", () -> {
        setupSkateGlintTexturing(1.6F);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });

    public static final RenderType SKATEBOARD_GLINT = create("skateboard_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(SKATEBOARD_GLINT_TEXTURE, true, false)).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(SKATE_GLINT_TEXTURING).createCompositeState(false));

    private static void setupSkateGlintTexturing(float scale) {
        long i = Util.getMillis() * 10L;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = (new Matrix4f()).translation(-f, f1, 0.0F);
        matrix4f.rotateZ(0.17453292F).scale(scale);
        RenderSystem.setTextureMatrix(matrix4f);

    }


    public static RenderType getHover(ResourceLocation texture) {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLayeringState(NO_LAYERING)
                .createCompositeState(false);
        return create("hover", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
    }

    public static RenderType getEmissiveWheels(ResourceLocation texture) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(RenderStateShard.COLOR_WRITE).setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST).setOverlayState(OVERLAY).createCompositeState(true);
        return create("emissivewheels", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
    }
}
