package com.github.alexthe668.iwannaskate.server.blockentity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.block.IWSBlockRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSBlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, IWannaSkateMod.MODID);

    public static final RegistryObject<BlockEntityType<SkateboardRackBlockEntity>> SKATEBOARD_RACK = DEF_REG.register("skateboard_rack", () -> BlockEntityType.Builder.of(SkateboardRackBlockEntity::new, IWSBlockRegistry.SKATEBOARD_RACK.get()).build(null));

}
