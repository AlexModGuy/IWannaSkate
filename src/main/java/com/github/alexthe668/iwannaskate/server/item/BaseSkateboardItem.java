package com.github.alexthe668.iwannaskate.server.item;

import com.github.alexthe666.citadel.item.ItemWithHoverAnimation;
import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class BaseSkateboardItem extends Item implements ItemWithHoverAnimation {

    private final ImmutableMultimap<Attribute, AttributeModifier> weaponModifiers;

    public BaseSkateboardItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)2F, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)-3.2F, AttributeModifier.Operation.ADDITION));
        this.weaponModifiers = builder.build();
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.weaponModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> itemStacks) {
        if (this.allowedIn(tab)) {
            SkateboardMaterials.getSkateboardMaterials().forEach(item -> addBoardToTab(itemStacks, BaseSkateboardItem.this, item));
        }
    }

    public void addBoardToTab(NonNullList<ItemStack> itemStacks, Item board, Item material) {
        ItemStack stack = new ItemStack(board);
        SkateboardData data = new SkateboardData(ForgeRegistries.ITEMS.getKey(material));
        SkateboardData.setStackData(stack, data);
        itemStacks.add(stack);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) IWannaSkateMod.PROXY.getISTERProperties());
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        SkateboardData.fromStack(stack).appendHoverText(tooltip, stack);
    }

    public boolean canFlipInInventory(ItemStack stack){
        return false;
    }

    public boolean isValidRepairItem(ItemStack stack1, ItemStack stack2) {
        return stack2.is(IWSItemRegistry.SKATEBOARD_TRUCK.get());
    }

    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) && (stack.getTag() == null || !stack.getTag().getBoolean("RemovedShimmer"));
    }

    @Override
    public float getMaxHoverOverTime(ItemStack itemStack) {
        return 5F;
    }

    @Override
    public boolean canHoverOver(ItemStack itemStack) {
        return false;
    }


}
