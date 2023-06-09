package com.github.alexthe668.iwannaskate.server.block;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSBlockRegistry {

    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, IWannaSkateMod.MODID);

    public static final RegistryObject<Block> SKATEBOARD_RACK = DEF_REG.register("skateboard_rack", () -> new SkateboardRackBlock(BlockBehaviour.Properties.of().strength(0.5F).pushReaction(PushReaction.DESTROY).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PIZZA = DEF_REG.register("pizza", () -> new PizzaBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOn().pushReaction(PushReaction.DESTROY).strength(0.5F).sound(SoundType.WOOL)));


}
