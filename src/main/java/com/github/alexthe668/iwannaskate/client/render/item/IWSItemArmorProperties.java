package com.github.alexthe668.iwannaskate.client.render.item;

import com.github.alexthe668.iwannaskate.client.model.IWSModelLayers;
import com.github.alexthe668.iwannaskate.client.model.SpikedSkaterHelmetModel;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class IWSItemArmorProperties implements IClientItemExtensions {

    public static SpikedSkaterHelmetModel SPIKED_SKATER_HELMET;
    private static boolean init;

    public static void initializeModels() {
        init = true;
        SPIKED_SKATER_HELMET = new SpikedSkaterHelmetModel(Minecraft.getInstance().getEntityModels().bakeLayer(IWSModelLayers.SPIKED_SKATER_HELMET));
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        if (!init) {
            initializeModels();
        }
        if(itemStack.is(IWSItemRegistry.SPIKED_SKATER_HELMET.get())){
            return SPIKED_SKATER_HELMET;
        }
        return _default;
    }
}