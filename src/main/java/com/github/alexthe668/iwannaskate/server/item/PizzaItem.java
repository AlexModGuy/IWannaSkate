package com.github.alexthe668.iwannaskate.server.item;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class PizzaItem extends IWSBlockItem{

    public PizzaItem(RegistryObject<Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}
