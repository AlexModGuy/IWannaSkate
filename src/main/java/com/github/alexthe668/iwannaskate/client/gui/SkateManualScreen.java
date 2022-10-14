package com.github.alexthe668.iwannaskate.client.gui;

import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SkateManualScreen extends GuiBasicBook {

    public SkateManualScreen(ItemStack book) {
        super(book, Component.translatable("item.iwannaskate.skating_manual"));
    }

    @Override
    protected int getBindingColor() {
        return 0XEB761D;
    }

    @Override
    protected int getTextColor() {
        return 9729114;
    }

    @Override
    public ResourceLocation getRootPage() {
        return new ResourceLocation(IWannaSkateMod.MODID, "book/skate_manual/root.json");
    }

    @Override
    public String getTextFileDirectory() {
        return IWannaSkateMod.MODID + ":book/skate_manual/";
    }
}
