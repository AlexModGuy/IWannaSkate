package com.github.alexthe668.iwannaskate.client.render.item;

import com.github.alexthe668.iwannaskate.client.model.BeanieModel;
import com.github.alexthe668.iwannaskate.client.model.IWSModelLayers;
import com.github.alexthe668.iwannaskate.client.model.SkaterCapModel;
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
    public static BeanieModel BEANIE;
    public static SkaterCapModel SKATER_CAP;
    private static boolean init;

    public static void initializeModels() {
        init = true;
        SPIKED_SKATER_HELMET = new SpikedSkaterHelmetModel(Minecraft.getInstance().getEntityModels().bakeLayer(IWSModelLayers.SPIKED_SKATER_HELMET));
        BEANIE = new BeanieModel(Minecraft.getInstance().getEntityModels().bakeLayer(IWSModelLayers.BEANIE));
        SKATER_CAP = new SkaterCapModel(Minecraft.getInstance().getEntityModels().bakeLayer(IWSModelLayers.SKATER_CAP));
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        if (!init) {
            initializeModels();
        }
        if(itemStack.is(IWSItemRegistry.SPIKED_SKATER_HELMET.get())){
            return SPIKED_SKATER_HELMET;
        }
        if(itemStack.is(IWSItemRegistry.BEANIE.get())){
            return BEANIE;
        }
        if(itemStack.is(IWSItemRegistry.SKATER_CAP.get())){
            return SKATER_CAP;
        }
        return _default;
    }
}