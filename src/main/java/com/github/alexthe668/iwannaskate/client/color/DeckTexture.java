package com.github.alexthe668.iwannaskate.client.color;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public class DeckTexture extends SimpleTexture {

    private int[] colors;
    private ResourceLocation[] textureLocs;

    private NativeImage coreImage;


    public DeckTexture(ResourceLocation[] textures, int[] colors) {
        super(textures[0]);

        this.textureLocs = textures;
        this.colors = colors;
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        for (int i = 0; i < textureLocs.length; i++) {
            loadSimpleTexture(resourceManager, textureLocs[i], colors[i]);
        }
        if(coreImage !=  null){
            TextureUtil.prepareImage(this.getId(), coreImage.getWidth(), coreImage.getHeight());
            this.bind();
            this.coreImage.upload(0, 0, 0, false);
        }

    }

    private void loadSimpleTexture(ResourceManager resourceManager, ResourceLocation resourceLocation, int color) throws IOException {
        SimpleTexture.TextureImage simpletexture$textureimage = SimpleTexture.TextureImage.load(resourceManager, resourceLocation);
        simpletexture$textureimage.throwIfError();
        NativeImage nativeimage = simpletexture$textureimage.getImage();
        setColorOfImage(nativeimage, color);
        addImageToCore(nativeimage);
    }

    private void setColorOfImage(NativeImage nativeImage, int color) {
        for (int i = 0; i < nativeImage.getWidth(); i++) {
            for (int j = 0; j < nativeImage.getHeight(); j++) {
                int colorAt = nativeImage.getPixelRGBA(i, j);
                int alpha = colorAt >> 24 & 0xFF;
                if (alpha != 255) {
                    continue;
                } else {
                    nativeImage.setPixelRGBA(i, j, color);
                }
            }
        }
    }

    private void addImageToCore(NativeImage nativeImage) {
        if(coreImage == null){
            coreImage = new NativeImage(nativeImage.getWidth(), nativeImage.getHeight(), false);
        }
        for (int i = 0; i < nativeImage.getWidth(); i++) {
            for (int j = 0; j < nativeImage.getHeight(); j++) {
                int colorAt = nativeImage.getPixelRGBA(i, j);
                int alpha = colorAt >> 24 & 0xFF;
                if (alpha == 0) {
                    continue;
                } else {
                    coreImage.setPixelRGBA(i, j, colorAt);
                }
            }
        }
    }

    public static ResourceLocation getOrCreateDeckTexture(ResourceLocation deckTexture, ResourceLocation[] textures, int[] colors) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstracttexture = textureManager.getTexture(deckTexture, MissingTextureAtlasSprite.getTexture());
        if (abstracttexture == MissingTextureAtlasSprite.getTexture() && textures.length == colors.length) {
            textureManager.register(deckTexture, new DeckTexture(textures, colors));
        }
        return deckTexture;
    }

}
