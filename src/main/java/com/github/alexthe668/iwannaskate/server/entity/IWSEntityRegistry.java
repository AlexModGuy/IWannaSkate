package com.github.alexthe668.iwannaskate.server.entity;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = IWannaSkateMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IWSEntityRegistry {
    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IWannaSkateMod.MODID);
    public static final RegistryObject<EntityType<SkateboardEntity>> SKATEBOARD = DEF_REG.register("skateboard", () -> (EntityType)EntityType.Builder.of(SkateboardEntity::new, MobCategory.MISC).sized(0.6F, 0.3125F).setCustomClientFactory(SkateboardEntity::new).setUpdateInterval(1).clientTrackingRange(10).build("skateboard"));
    public static final RegistryObject<EntityType<SkaterSkeletonEntity>> SKATER_SKELETON = DEF_REG.register("skater_skeleton", () -> (EntityType)EntityType.Builder.of(SkaterSkeletonEntity::new, MobCategory.MONSTER).sized(0.8F, 1.8F).build("skater_skeleton"));


    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        SpawnPlacements.register(SKATER_SKELETON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SkaterSkeletonEntity::checkMonsterSpawnRules);
        event.put(SKATER_SKELETON.get(), SkaterSkeletonEntity.createAttributes().build());

    }
}
