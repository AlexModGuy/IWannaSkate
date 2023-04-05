package com.github.alexthe668.iwannaskate.server.world;

import com.github.alexthe668.iwannaskate.IWannaSkateMod;
import com.github.alexthe668.iwannaskate.server.entity.IWSEntityRegistry;
import com.github.alexthe668.iwannaskate.server.entity.WanderingSkaterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

public class WanderingSkaterSpawner  {
    private final Random random = new Random();
    private final ServerLevel world;
    private int timer;
    private int delay;
    private float chance;

    public WanderingSkaterSpawner(ServerLevel level) {
        this.world = level;
        this.timer = 1200;
        IWSWorldData worldinfo = IWSWorldData.get(level);
        this.delay = worldinfo.getSkaterSpawnDelay();
        this.chance = worldinfo.getSkaterSpawnChance();
        if (this.delay == 0 && this.chance == 0) {
            this.delay = IWannaSkateMod.COMMON_CONFIG.wanderingSkaterSpawnDelay.get();
            worldinfo.setSkaterSpawnDelay(this.delay);
            this.chance = 3 * IWannaSkateMod.COMMON_CONFIG.wanderingSkaterSpawnChance.get().floatValue();
            worldinfo.setSkaterSpawnChance(this.chance);
        }

    }

    public void tick() {
        if (IWannaSkateMod.COMMON_CONFIG.spawnWanderingSkaters.get() && --this.timer <= 0) {
            this.timer = 1200;
            this.delay -= 1200;
            if(delay < 0){
                delay = 0;
            }
            IWSWorldData worldinfo = IWSWorldData.get(world);
            worldinfo.setSkaterSpawnDelay(this.delay);
            if (this.delay <= 0) {
                this.delay = IWannaSkateMod.COMMON_CONFIG.wanderingSkaterSpawnDelay.get();
                if (this.world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.world.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
                    float prevChance = this.chance;
                    this.chance = Mth.clamp(this.chance + IWannaSkateMod.COMMON_CONFIG.wanderingSkaterSpawnChance.get().floatValue(), 0, 1F);
                    worldinfo.setSkaterSpawnChance(this.chance);
                    if (this.random.nextFloat() <= prevChance && this.attemptSpawnWanderingSkater()) {
                        this.chance = IWannaSkateMod.COMMON_CONFIG.wanderingSkaterSpawnChance.get().floatValue();
                    }
                }
            }
        }

    }

    private boolean attemptSpawnWanderingSkater() {
        IWSWorldData worldinfo = IWSWorldData.get(world);
        Player playerentity = this.world.getRandomPlayer();
        if (playerentity == null) {
            return true;
        } else if (this.random.nextInt(5) != 0) {
            return false;
        } else {
            BlockPos blockpos = BlockPos.containing(playerentity.position());
            PoiManager poimanager = this.world.getPoiManager();
            Optional<BlockPos> optional = poimanager.find((p_219713_) -> {
                return p_219713_.is(PoiTypes.MEETING);
            }, (p_219711_) -> {
                return true;
            }, blockpos, 48, PoiManager.Occupancy.ANY);
            BlockPos blockpos1 = optional.orElse(blockpos);
            BlockPos blockpos2 = this.findSpawnLocationNear(blockpos1, 84);
            if (blockpos2 != null && this.func_226559_a_(blockpos2) && blockpos2.distSqr(blockpos) > 225) {
                if (blockpos2 != null && this.hasEnoughSpace(world, blockpos2)) {
                    if (world.getBiome(blockpos2).is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                        return false;
                    }

                    WanderingSkaterEntity skater = IWSEntityRegistry.WANDERING_SKATER.get().spawn(world, blockpos2, MobSpawnType.EVENT);
                    if (skater != null) {
                        if(worldinfo != null){
                            worldinfo.setSkaterUUID(skater.getUUID());
                        }
                        skater.setDespawnDelay(48000);
                        skater.setWanderTarget(blockpos1);
                        skater.restrictTo(blockpos1, 22);
                        return true;
                    }
                }

                return true;
            }
            return false;
        }
    }

    @Nullable
    private BlockPos findSpawnLocationNear(BlockPos center, int xzDistance) {
        BlockPos blockpos = null;

        for(int i = 0; i < 10; ++i) {
            int j = center.getX() + this.random.nextInt(xzDistance * 2) - xzDistance;
            int k = center.getZ() + this.random.nextInt(xzDistance * 2) - xzDistance;
            int l = this.world.getHeight(Heightmap.Types.WORLD_SURFACE, j, k);
            BlockPos blockpos1 = new BlockPos(j, l, k);
            if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.world, blockpos1, IWSEntityRegistry.WANDERING_SKATER.get())) {
                blockpos = blockpos1;
                break;
            }
        }

        return blockpos;
    }

    private boolean func_226559_a_(BlockPos p_226559_1_) {
        Iterator var2 = BlockPos.betweenClosed(p_226559_1_, p_226559_1_.offset(1, 2, 1)).iterator();

        BlockPos blockpos;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            blockpos = (BlockPos)var2.next();
        } while(this.world.getBlockState(blockpos).getBlockSupportShape(this.world, blockpos).isEmpty() && world.getFluidState(blockpos).isEmpty());

        return false;
    }


    private boolean hasEnoughSpace(BlockGetter level, BlockPos pos) {
        for(BlockPos blockpos : BlockPos.betweenClosed(pos, pos.offset(1, 2, 1))) {
            if (!level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
