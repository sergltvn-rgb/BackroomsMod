package com.litvin.backrooms.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Правила естественного спавна мобов Закулисья.
 * Регистрируются через SpawnPlacements в ModEvents (FMLCommonSetupEvent).
 */
public class BackroomsSpawnRules {

    /**
     * Bacteria может спавниться при любом освещении (она не боится света),
     * но только на твёрдом полу в свободном пространстве и не на мирном уровне сложности.
     */
    public static boolean checkBacteriaSpawn(EntityType<BacteriaEntity> type, ServerLevelAccessor level,
                                             MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /**
     * Smiler спавнится ТОЛЬКО в темноте (блочный свет <= 5),
     * иначе он мгновенно исчезает при свете > 5 (см. SmilerEntity#tick).
     */
    public static boolean checkSmilerSpawn(EntityType<SmilerEntity> type, ServerLevelAccessor level,
                                           MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBrightness(LightLayer.BLOCK, pos) <= 5
                && Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkSkinStealerSpawn(EntityType<SkinStealerEntity> type, ServerLevelAccessor level,
                                             MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Monster.checkAnyLightMonsterSpawnRules(type, level, spawnType, pos, random);
    }
}
