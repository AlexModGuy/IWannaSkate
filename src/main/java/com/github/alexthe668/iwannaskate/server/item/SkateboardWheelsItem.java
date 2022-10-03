package com.github.alexthe668.iwannaskate.server.item;

import net.minecraft.world.item.Item;

public class SkateboardWheelsItem extends Item {

    private SkateboardWheels wheelType;

    public SkateboardWheelsItem(Item.Properties tab, SkateboardWheels wheelType) {
        super(tab);
        this.wheelType = wheelType;
    }

    public SkateboardWheels getWheelType(){
        return wheelType;
    }
}
