package com.github.alexthe668.iwannaskate.client.render.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class IWSItemRenderProperties implements IClientItemExtensions {

    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return new IWSItemstackRenderer();
    }
}