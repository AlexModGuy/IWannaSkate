package com.github.alexthe668.iwannaskate.server.enchantment;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.SkateboardItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IWSEnchantmentRegistry {

    public static final DeferredRegister<Enchantment> DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, IWannaSkateMod.MODID);
    public static final EnchantmentCategory SKATEBOARD = EnchantmentCategory.create("skateboard", (item -> item instanceof SkateboardItem));

    public static final RegistryObject<Enchantment> SIDEWINDER = DEF_REG.register("sidewinder", () -> new SkateboardEnchantment("sidewinder", Enchantment.Rarity.COMMON, 2, 6));
    public static final RegistryObject<Enchantment> CLAMBERING = DEF_REG.register("clambering", () -> new SkateboardEnchantment("clambering", Enchantment.Rarity.UNCOMMON, 1, 11));
    public static final RegistryObject<Enchantment> INERTIAL = DEF_REG.register("inertial", () -> new SkateboardEnchantment("inertial", Enchantment.Rarity.COMMON, 3, 8));
    public static final RegistryObject<Enchantment> PEDALLING = DEF_REG.register("pedalling", () -> new SkateboardEnchantment("pedalling", Enchantment.Rarity.COMMON, 2, 6));
    public static final RegistryObject<Enchantment> AERIAL = DEF_REG.register("aerial", () -> new SkateboardEnchantment("aerial", Enchantment.Rarity.UNCOMMON, 4, 8));
    public static final RegistryObject<Enchantment> SECURED = DEF_REG.register("secured", () -> new SkateboardEnchantment("secured", Enchantment.Rarity.UNCOMMON, 1, 12));
    public static final RegistryObject<Enchantment> EARTHCROSSER = DEF_REG.register("earthcrosser", () -> new SkateboardEnchantment("earthcrosser", Enchantment.Rarity.RARE, 1, 11));
    public static final RegistryObject<Enchantment> SURFING = DEF_REG.register("surfing", () -> new SkateboardEnchantment("surfing", Enchantment.Rarity.RARE, 1, 16));
    public static final RegistryObject<Enchantment> HARDWOOD = DEF_REG.register("hardwood", () -> new SkateboardEnchantment("hardwood", Enchantment.Rarity.UNCOMMON, 1, 12));
    public static final RegistryObject<Enchantment> BASHING = DEF_REG.register("bashing", () -> new SkateboardEnchantment("bashing", Enchantment.Rarity.UNCOMMON, 4, 8));
    public static final RegistryObject<Enchantment> ONBOARDING = DEF_REG.register("onboarding", () -> new SkateboardEnchantment("onboarding", Enchantment.Rarity.UNCOMMON, 1, 14));
    public static final RegistryObject<Enchantment> BENTHIC = DEF_REG.register("benthic", () -> new SkateboardEnchantment("benthic", Enchantment.Rarity.RARE, 1, 16));

    public static boolean areCompatible(Enchantment enchantment1, Enchantment enchantment2) {
        if(enchantment1 == SURFING.get()){
            return enchantment2 != BENTHIC.get();
        }
        if(enchantment1 == BENTHIC.get()){
            return enchantment2 != SURFING.get();
        }
        return true;
    }
}
