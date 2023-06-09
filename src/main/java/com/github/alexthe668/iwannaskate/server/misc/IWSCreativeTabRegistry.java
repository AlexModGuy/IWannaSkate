package com.github.alexthe668.iwannaskate.server.misc;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.enchantment.IWSEnchantmentRegistry;
import com.github.alexthe668.iwannaskate.server.item.CustomTabBehavior;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSCreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IWannaSkateMod.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = DEF_REG.register("iwannaskate", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.iwannaskate"))
            .icon(IWSCreativeTabRegistry::makeIcon)
            .displayItems((enabledFeatures, output) -> {
                for(RegistryObject<Item> item : IWSItemRegistry.DEF_REG.getEntries()){
                    if(item.get() instanceof CustomTabBehavior customTabBehavior){
                        customTabBehavior.fillItemCategory(output);
                    }else{
                        output.accept(item.get());
                    }
                }
                for(SkateboardWheels wheels : SkateboardWheels.values()){
                    output.accept(wheels.getItemRegistryObject().get());
                }
                for(Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()){
                    if(enchantment.category == IWSEnchantmentRegistry.SKATEBOARD){
                        output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
                    }
                }
            })
            .build());

    private static ItemStack makeIcon() {
        ItemStack stack = new ItemStack(IWSItemRegistry.SKATEBOARD.get());
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IsCreativeTab", true);
        stack.setTag(tag);
        return stack;
    }
}
