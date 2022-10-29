package com.github.alexthe668.iwannaskate.server.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SkateboardWheelsItem extends Item {

    private SkateboardWheels wheelType;

    public SkateboardWheelsItem(Item.Properties tab, SkateboardWheels wheelType) {
        super(tab);
        this.wheelType = wheelType;
    }

    public SkateboardWheels getWheelType(){
        return wheelType;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (wheelType == SkateboardWheels.EMERALD) {
            tooltip.add(Component.translatable("item.iwannaskate.skateboard_wheels_emerald.desc").withStyle(ChatFormatting.GRAY));
        }
    }
}
