package com.litvin.backrooms.event;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.capability.SanityProvider;
import com.litvin.backrooms.entity.BacteriaEntity;
import com.litvin.backrooms.entity.SmilerEntity;
import com.litvin.backrooms.init.SoundInit;
import com.litvin.backrooms.network.ClientboundSanitySyncPacket;
import com.litvin.backrooms.network.PacketHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BackroomsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    // Счётчик тиков "застревания в стенах" для механики ноклипа
    private static final java.util.Map<java.util.UUID, Integer> NOCLIP_TICKS = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(SanityProvider.SANITY).isPresent()) {
                event.addCapability(new ResourceLocation(BackroomsMod.MODID, "sanity"), new SanityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(SanityProvider.SANITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(SanityProvider.SANITY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                    newStore.setSanity(100.0f); // Восстанавливаем рассудок после смерти
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                PacketHandler.sendToClient(new ClientboundSanitySyncPacket(sanity.getSanity(), sanity.isHidden()), player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                PacketHandler.sendToClient(new ClientboundSanitySyncPacket(sanity.getSanity(), sanity.isHidden()), player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                PacketHandler.sendToClient(new ClientboundSanitySyncPacket(sanity.getSanity(), sanity.isHidden()), player);
            });
        }
    }

    // ==================== КАСТОМНЫЕ СООБЩЕНИЯ О СМЕРТИ ====================

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (player.getServer() == null) return;

        Entity killer = event.getSource().getEntity();
        String name = player.getGameProfile().getName();
        Component message = null;

        if (killer instanceof SmilerEntity) {
            String[] variants = {
                    name + " увидел улыбку в темноте",
                    name + " не смог отвести взгляд от Смайлера",
                    name + " был разорван улыбающейся тварью"
            };
            message = Component.literal(variants[player.getRandom().nextInt(variants.length)]);
        } else if (killer instanceof BacteriaEntity) {
            String[] variants = {
                    name + " растворился во плоти Бактерии",
                    name + " был поглощён в темноте",
                    name + " услышал крик слишком поздно"
            };
            message = Component.literal(variants[player.getRandom().nextInt(variants.length)]);
        } else if (player.level().dimension().location().getNamespace().equals(BackroomsMod.MODID)) {
            // Смерть в Бэкрумах от иных причин
            String[] variants = {
                    name + " сгинул в бесконечных коридорах",
                    name + " больше никогда не нашёл выход",
                    name + " потерял рассудок среди жёлтых стен"
            };
            message = Component.literal(variants[player.getRandom().nextInt(variants.length)]);
        }

        if (message != null) {
            player.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
    }

    // ===== Отмена урона от удушья в стенах в Закулисье (чтобы игрок успел провалиться) =====
    @SubscribeEvent
    public static void onLivingAttack(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        if (event.getEntity() instanceof Player p
                && p.level().dimension().location().getNamespace().equals(BackroomsMod.MODID)
                && event.getSource().is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            event.setCanceled(true);
        }
    }

    // Игрок зажат внутри твёрдых блоков (голова и ноги в стене)
    private static boolean isStuckInBlocks(ServerPlayer player) {
        Level level = player.level();
        net.minecraft.core.BlockPos feet = player.blockPosition();
        net.minecraft.core.BlockPos head = feet.above();
        return level.getBlockState(feet).isSuffocating(level, feet)
                && level.getBlockState(head).isSuffocating(level, head);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase == TickEvent.Phase.START) return;

        Player player = event.player;
        Level level = player.level();
        ResourceLocation dimension = level.dimension().location();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (dimension.getNamespace().equals(BackroomsMod.MODID)) {
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                // Безопасная комната: рассудок восстанавливается, эффекты и фантомы не появляются
                if (com.litvin.backrooms.util.BackroomsTeleporter.isInSafeRoom(player)) {
                    float oldSanity = sanity.getSanity();
                    sanity.addSanity(0.1f);
                    float restored = sanity.getSanity();
                    if (oldSanity != restored) {
                        PacketHandler.sendToClient(new ClientboundSanitySyncPacket(restored, sanity.isHidden()), serverPlayer);
                    }
                    return;
                }

                // ===== NOCLIP: застревание в блоках => провал на следующий уровень =====
                if (!player.isCreative() && !player.isSpectator() && isStuckInBlocks(serverPlayer)) {
                    java.util.UUID id = player.getUUID();
                    int stuck = NOCLIP_TICKS.getOrDefault(id, 0) + 1;
                    NOCLIP_TICKS.put(id, stuck);
                    if (stuck == 15) {
                        serverPlayer.sendSystemMessage(Component.literal("\u00a77Реальность начинает плыть..."));
                        level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_AMBIENT, SoundSource.AMBIENT, 0.6F, 0.4F);
                    }
                    if (stuck >= 35) {
                        NOCLIP_TICKS.remove(id);
                        com.litvin.backrooms.util.LevelTransitionManager.noclipToNextLevel(serverPlayer);
                    }
                    return;
                } else {
                    NOCLIP_TICKS.remove(player.getUUID());
                }

                int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());
                // Потеря рассудка сильно замедлена: ~0.2/сек в темноте, ~0.06/сек на свету
                float dropRate = (lightLevel < 5) ? 0.01f : 0.003f;

                sanity.addSanity(-dropRate);

                float currentSanity = sanity.getSanity();

                // Synchronize sanity to the client player
                PacketHandler.sendToClient(new ClientboundSanitySyncPacket(currentSanity, sanity.isHidden()), serverPlayer);

                // ============ АТМОСФЕРНЫЕ ЗВУКИ ПО УРОВНЮ РАССУДКА ============

                if (level.random.nextInt(600) == 0) {
                    level.playSound(null, player.blockPosition(), SoundInit.AMBIENT_VHS.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
                }

                if (currentSanity < 50.0f && level.random.nextInt(400) == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
                }

                // <60: редкие далёкие шёпоты/гул
                if (currentSanity < 60.0f && level.random.nextInt(700) == 0) {
                    level.playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.get(), SoundSource.AMBIENT,
                            0.6F, 0.5F + level.random.nextFloat() * 0.3F);
                }

                if (currentSanity < 35.0f) {
                    if (level.random.nextInt(300) == 0 && !player.hasEffect(com.litvin.backrooms.init.EffectInit.CAMERA_SHAKE.get())) {
                        int amp = currentSanity < 15.0f ? 1 : 0;
                        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(com.litvin.backrooms.init.EffectInit.CAMERA_SHAKE.get(), 60, amp, false, false));
                    }

                    // 1. Призрачные шаги за спиной
                    if (level.random.nextInt(400) == 0) {
                        double yaw = Math.toRadians(player.getYRot());
                        double dx = Math.sin(yaw) * 4.0D;
                        double dz = -Math.cos(yaw) * 4.0D;
                        net.minecraft.core.BlockPos stepsPos = player.blockPosition().offset((int) dx, 0, (int) dz);

                        if (level.random.nextBoolean()) {
                            level.playSound(null, stepsPos, SoundEvents.WOOL_STEP, SoundSource.AMBIENT, 1.2F, 0.8F + level.random.nextFloat() * 0.4F);
                        } else {
                            level.playSound(null, stepsPos, SoundEvents.GHAST_WARN, SoundSource.AMBIENT, 0.5F, 0.5F + level.random.nextFloat() * 0.3F);
                        }
                    }

                    // 2. Сердцебиение — тревожный пульс
                    if (level.random.nextInt(300) == 0) {
                        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.AMBIENT,
                                0.9F, 1.0F);
                    }

                    // 3. Теневые фантомы, исчезающие при взгляде
                    if (level.random.nextInt(1000) == 0) {
                        double angle = level.random.nextDouble() * 2.0D * Math.PI;
                        double dist = 10.0D + level.random.nextDouble() * 5.0D;
                        double spawnX = player.getX() + Math.cos(angle) * dist;
                        double spawnZ = player.getZ() + Math.sin(angle) * dist;
                        double spawnY = player.getY();

                        net.minecraft.core.BlockPos spawnPos = new net.minecraft.core.BlockPos((int) spawnX, (int) spawnY, (int) spawnZ);
                        if (level.isEmptyBlock(spawnPos) && level.isEmptyBlock(spawnPos.above()) && level.getBlockState(spawnPos.below()).isSolid()
                                && level.getMaxLocalRawBrightness(spawnPos) < 7) {
                            BacteriaEntity phantom = new BacteriaEntity(com.litvin.backrooms.init.EntityInit.BACTERIA.get(), level);
                            phantom.moveTo(spawnX + 0.5D, spawnY, spawnZ + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
                            phantom.setPhantom(true);
                            level.addFreshEntity(phantom);
                        }
                    }
                }

                if (currentSanity < 25.0f) {
                    if (level.random.nextInt(600) == 0) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, false));
                        level.playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.get(), SoundSource.AMBIENT, 1.0F, 0.5F);
                    }
                    // Далёкий крик Бактерии — рассудок на исходе
                    if (level.random.nextInt(900) == 0) {
                        level.playSound(null, player.blockPosition(), SoundInit.BACTERIA_SCREAM.get(), SoundSource.AMBIENT,
                                0.5F, 0.7F + level.random.nextFloat() * 0.2F);
                    }
                }
            });
        } else {
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                float oldSanity = sanity.getSanity();
                sanity.addSanity(0.05f);
                float currentSanity = sanity.getSanity();

                // Sync only if it has actually changed/increased
                if (oldSanity != currentSanity) {
                    PacketHandler.sendToClient(new ClientboundSanitySyncPacket(currentSanity, sanity.isHidden()), serverPlayer);
                }
            });
        }
    }
}
