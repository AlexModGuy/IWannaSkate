package com.github.alexthe668.iwannaskate.server.misc;

import com.google.common.collect.ImmutableList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.List;

public enum SkateQuality {

    LOW(0.3, IWSTags.LOW_SKATE_QUALITY, Material.DIRT, Material.GRASS, Material.PLANT, Material.MOSS, Material.SAND, Material.SCULK, Material.SNOW, Material.CLAY, Material.CACTUS, Material.POWDER_SNOW),
    MEDIUM(0.92F, IWSTags.MID_SKATE_QUALITY, Material.WOOD, Material.WOOL, Material.NETHER_WOOD, Material.AMETHYST),
    HIGH(0.935F, IWSTags.HIGH_SKATE_QUALITY, Material.STONE, Material.GLASS, Material.SHULKER_SHELL),
    BEST(0.95F, IWSTags.BEST_SKATE_QUALITY, Material.METAL, Material.HEAVY_METAL);

    private final double inertia;
    private final TagKey<Block> blockTag;
    private final List<Material> materialList;

    SkateQuality(double intertia, TagKey<Block> blockTag, Material... materials) {
        this.inertia = intertia;
        this.blockTag = blockTag;
        materialList = ImmutableList.copyOf(materials);
    }

    public double getInertia(){
        return inertia;
    }

    public static SkateQuality getSkateQuality(BlockState state, SkateQuality floor){
        if(state.isAir()){
            return HIGH;
        }
        SkateQuality tagMatch = null;
        for(SkateQuality value : values()){
            if(value.ordinal() >= floor.ordinal() && state.is(value.blockTag)){
                tagMatch = value;
            }
        }
        if(tagMatch != null){
            return tagMatch;
        }else{
            SkateQuality materialMatch = null;
            for(SkateQuality value : values()){
                if(value.ordinal() >= floor.ordinal() && value.materialList.contains(state.getMaterial())){
                    materialMatch = value;
                }
            }
            return materialMatch == null ? floor : materialMatch;
        }
    }
}
