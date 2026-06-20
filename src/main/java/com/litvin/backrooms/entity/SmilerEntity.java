package com.litvin.backrooms.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

public class SmilerEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SmilerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROZEN =
            SynchedEntityData.defineId(SmilerEntity.class, EntityDataSerializers.BOOLEAN);
    private int attackAnimTicks = 0;

    public SmilerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(FROZEN, false);
    }

    public void setFrozen(boolean frozen) {
        this.entityData.set(FROZEN, frozen);
    }

    public boolean isFrozen() {
        return this.entityData.get(FROZEN);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 70.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.4D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // Поведение «плачущего ангела»: замирает под взглядом, подкрадывается и телепортируется
        this.goalSelector.addGoal(2, new SmilerStalkGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                entity -> {
                    if (entity instanceof Player player) {
                        return player.getCapability(com.litvin.backrooms.capability.SanityProvider.SANITY).map(s -> !s.isHidden()).orElse(true);
                    }
                    return true;
                }));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SmilerEntity> event) {
        if (this.entityData.get(ATTACKING)) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.smiler.jumpscare"));
            return PlayState.CONTINUE;
        }
        // Замерший Смайлер стоит абсолютно неподвижно
        if (this.isFrozen()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.smiler.idle"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.smiler.walk"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.smiler.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockState) {
        // Смайлер бесшумен при ходьбе
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.level().getMaxLocalRawBrightness(this.blockPosition()) > 5) {
                this.discard();
                return;
            }

            if (this.attackAnimTicks > 0) {
                this.attackAnimTicks--;
                if (this.attackAnimTicks == 0) {
                    this.entityData.set(ATTACKING, false);
                }
            }

            Player player = this.level().getNearestPlayer(this, 24.0D);
            if (player instanceof ServerPlayer sp && this.distanceToSqr(sp) < 144.0D) {
                // Аура тьмы: вблизи Смайлера экран затягивает мраком
                sp.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));
            }

            // Шёпот и жуткие звуки
            if (player != null) {
                double distSqr = this.distanceToSqr(player);
                // Дыхание/шёпот вблизи — чаще, когда рядом
                if (distSqr < 100.0D && this.random.nextInt(60) == 0) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.WARDEN_AMBIENT, SoundSource.HOSTILE, 0.35F, 1.6F + this.random.nextFloat() * 0.3F);
                }
                if (this.random.nextInt(35) == 0) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 0.6F, 0.4F + this.random.nextFloat() * 0.3F);
                    if (this.random.nextBoolean()) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.PORTAL_AMBIENT, SoundSource.HOSTILE, 0.4F, 0.5F + this.random.nextFloat() * 0.3F);
                    }
                }
                // Редкий жуткий смех/визг, когда замер под взглядом
                if (this.isFrozen() && this.random.nextInt(50) == 0) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 0.5F, 0.7F);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            this.entityData.set(ATTACKING, true);
            this.attackAnimTicks = 12;
        }
        if (hurt && target instanceof ServerPlayer serverPlayer) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));

            serverPlayer.getCapability(com.litvin.backrooms.capability.SanityProvider.SANITY).ifPresent(sanity -> {
                sanity.addSanity(-15.0F);
                com.litvin.backrooms.network.PacketHandler.sendToClient(
                        new com.litvin.backrooms.network.ClientboundSanitySyncPacket(sanity.getSanity(), sanity.isHidden()),
                        serverPlayer
                );
            });

            serverPlayer.level().playSound(null, serverPlayer.blockPosition(),
                    SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.5F, 0.7F);

            com.litvin.backrooms.network.PacketHandler.sendToClient(
                    new com.litvin.backrooms.network.ClientboundJumpscarePacket(),
                    serverPlayer
            );
        }
        return hurt;
    }
}
