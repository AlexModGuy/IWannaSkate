package com.github.alexthe668.iwannaskate.client.render.entity;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public class DeckTexture extends AbstractTexture {

    private int[] colors;
    private SimpleTexture[] simpleTextures;

    public DeckTexture(ResourceLocation[] textures, int[] colors) {
        for(int i = 0; i < textures.length; i++){
            simpleTextures[i] = new SimpleTexture(textures[i]);
        }
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        for(int i = 0; i < simpleTextures.length; i++){
            simpleTextures[i].load(resourceManager);
        }

    }
}
