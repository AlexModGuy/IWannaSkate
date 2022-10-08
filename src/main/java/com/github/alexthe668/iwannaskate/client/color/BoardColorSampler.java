package com.github.alexthe668.iwannaskate.client.color;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;


public class BoardColorSampler {

    private static final int COLORS_TO_SAMPLE = 4;
    private static final int[] DEFAULT_COLORS = new int[]{0XC29D62, 0XB8945F, 0XAF8F55, 0X9F844D};
    public static Map<ResourceLocation, int[]> TEXTURES_TO_COLOR = new HashMap<>();

    public static void sampleColorsOnLoad(){
        ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(IWSTags.SAMPLE_COLORS_ON_LOAD)).forEach(BoardColorSampler::getColor);
    }

    public static int[] getColor(ResourceLocation item) {
        return getColor(ForgeRegistries.ITEMS.getValue(item));
    }

    public static int[] getColor(Item item) {
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(item);
        if(name == null){
            return DEFAULT_COLORS;
        }
        if (TEXTURES_TO_COLOR.get(name) != null) {
            return TEXTURES_TO_COLOR.get(name);
        } else {
            int colorizer = -1;
            try{
                colorizer = Minecraft.getInstance().getItemColors().getColor(new ItemStack(item), 0);
            }catch (Exception e){
                IWannaSkateMod.LOGGER.warn("Another mod did not use item colorizers correctly.");
            }
            int[] colors = new int[COLORS_TO_SAMPLE];
            try {
                List<Integer> gatherAllColors = gatherAllColors(getTextureAtlasSprite(item));
                if(gatherAllColors.isEmpty()){
                    colors = DEFAULT_COLORS;
                }else{
                    for(int i = 0; i < COLORS_TO_SAMPLE; i++){
                        int color = 0;
                        if(gatherAllColors.size() < i + 1){
                            color = gatherAllColors.get(i - 1);
                        }else{
                            color = gatherAllColors.get(i);
                        }
                        colors[i] = color;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                colors = DEFAULT_COLORS;
            }

            TEXTURES_TO_COLOR.put(name, colors);
            return colors;
        }
    }


    private static List<Integer> gatherAllColors(TextureAtlasSprite image) {
        List<Integer> colors = new ArrayList<>();
        int uMax = image.getWidth();
        int vMax = image.getHeight();
        for (float i = 0; i < uMax; i++) {
            for (float j = 0; j < vMax; j++) {
                int color = image.getPixelRGBA(0, (int) i, (int) j);
                int alpha = color >> 24 & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                if (!colors.contains(color)) {
                    colors.add(color);
                }
            }
        }
        Collections.sort(colors, (a,b) -> Integer.compare(b, a));
        return colors;
    }


    private static TextureAtlasSprite getTextureAtlasSprite(Item itemStack) {
        return Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack).getParticleIcon(ModelData.EMPTY);
    }
}
