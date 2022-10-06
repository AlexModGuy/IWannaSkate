package com.github.alexthe668.iwannaskate.server;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardMaterials;
import com.github.alexthe668.iwannaskate.server.recipe.IWSRecipeRegistry;
import com.github.alexthe668.iwannaskate.server.world.WanderingSkaterSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IWannaSkateMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
    private static final Map<ServerLevel, WanderingSkaterSpawner> WANDERING_SKATER_SPAWNER_MAP = new HashMap<>();

    public void init() {
    }

    public void clientInit() {
    }

    public Object getISTERProperties() {
        return null;
    }


    @SubscribeEvent
    public void onTagsLoaded(TagsUpdatedEvent event){
        SkateboardMaterials.reload();
        IWSRecipeRegistry.registerCauldronInteractions();
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        double rate = IWannaSkateMod.COMMON_CONFIG.skateboardExpAnvilRateModifier.get();
        if(event.getLeft().is(IWSItemRegistry.SKATEBOARD.get()) && event.getRight().is(Items.ENCHANTED_BOOK) && rate != 1.0D && event.getCost() > 1){
            int initialCost = event.getCost();
            ItemStack copy = event.getLeft().copy();
            boolean incompatible = false;
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(copy);
            Map<Enchantment, Integer> map1 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(event.getRight()));
            for(Map.Entry<Enchantment, Integer> entry : map1.entrySet()){
                int before = map.getOrDefault(entry.getKey(), 0);
                for(Map.Entry<Enchantment, Integer> entry2 : map.entrySet()){
                    if(entry.getKey() != entry2.getKey() && !entry.getKey().isCompatibleWith(entry2.getKey())){
                        incompatible = true;
                    }
                }
                if(!incompatible){
                    initialCost += entry.getKey().getMaxCost(entry.getValue());
                    map.put(entry.getKey(), before + entry.getValue());
                }
            }
            if(!incompatible && initialCost > 5){
                EnchantmentHelper.setEnchantments(map, copy);
                event.setOutput(copy);
                event.setCost(Math.max(1, (int) Math.ceil(initialCost * rate)));
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.LevelTickEvent tick) {
        if (!tick.level.isClientSide && tick.level instanceof ServerLevel serverWorld) {
            WANDERING_SKATER_SPAWNER_MAP.computeIfAbsent(serverWorld,
                    k -> new WanderingSkaterSpawner(serverWorld));
            WanderingSkaterSpawner spawner = WANDERING_SKATER_SPAWNER_MAP.get(serverWorld);
            spawner.tick();
        }
    }

    public void setHoverItem(ItemStack stack){}

    public Player getClientSidePlayer() {
        return null;
    }

    public boolean isKeyDown(int keyType) {
        return false;
    }

    public void onEntityStatus(Entity entity, byte updateKind) {
    }

    public void reloadConfig() {
    }
}
