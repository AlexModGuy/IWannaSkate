package com.github.alexthe668.iwannaskate.compat.jei;

import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class SkateboardSubtypeInterpreter  implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final SkateboardSubtypeInterpreter INSTANCE = new SkateboardSubtypeInterpreter();

    private SkateboardSubtypeInterpreter() {

    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        SkateboardData data = SkateboardData.fromStack(itemStack);
        return data.getWoodBlock().toString();
    }
}
