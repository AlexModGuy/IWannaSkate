package com.github.alexthe668.iwannaskate.server.misc;

import com.google.common.collect.ImmutableList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public enum SkateQuality {

    LOW(0.3, IWSTags.LOW_SKATE_QUALITY),
    MEDIUM(0.92F, IWSTags.MID_SKATE_QUALITY, SoundType.WOOD, SoundType.WOOL, SoundType.NETHER_WOOD, SoundType.BAMBOO_WOOD, SoundType.CHERRY_WOOD, SoundType.AMETHYST, SoundType.BASALT),
    HIGH(0.935F, IWSTags.HIGH_SKATE_QUALITY, SoundType.STONE, SoundType.GLASS, SoundType.CALCITE),
    BEST(0.95F, IWSTags.BEST_SKATE_QUALITY, SoundType.METAL);

    private final double inertia;
    private final TagKey<Block> blockTag;
    private final List<SoundType> materialList;

    SkateQuality(double intertia, TagKey<Block> blockTag, SoundType... materials) {
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
                if(value.ordinal() >= floor.ordinal() && value.materialList.contains(state.getSoundType())){
                    materialMatch = value;
                }
            }
            return materialMatch == null ? floor : materialMatch;
        }
    }
}
