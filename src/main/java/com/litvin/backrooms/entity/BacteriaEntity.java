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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import com.litvin.backrooms.init.SoundInit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BacteriaEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Boolean> JUMPSCARING = SynchedEntityData.defineId(BacteriaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PHANTOM = SynchedEntityData.defineId(BacteriaEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int jumpscareCooldown = 0;
    private int phantomLifeTime = 400; // 20 seconds
    private int soundCooldown = 0;

    public BacteriaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JUMPSCARING, false);
        this.entityData.define(PHANTOM, false);
    }

    public boolean isJumpscaring() {
        return this.entityData.get(JUMPSCARING);
    }

    public void setJumpscaring(boolean value) {
        this.entityData.set(JUMPSCARING, value);
    }

    public boolean isPhantom() {
        return this.entityData.get(PHANTOM);
    }

    public void setPhantom(boolean value) {
        this.entityData.set(PHANTOM, value);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.isPhantom()) {
                this.phantomLifeTime--;
                if (this.phantomLifeTime <= 0) {
                    this.discard();
                    return;
                }

                this.setTarget(null);

                Player player = this.level().getNearestPlayer(this, 16.0D);
                if (player != null) {
                    net.minecraft.world.phys.Vec3 toPhantom = this.position().subtract(player.position()).normalize();
                    net.minecraft.world.phys.Vec3 lookVec = player.getLookAngle();
                    double dot = lookVec.dot(toPhantom);

                    if (dot > 0.85D) {
                        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF, this.getX(), this.getY() + 1.0D, this.getZ(), 20, 0.5D, 1.0D, 0.5D, 0.05D);
                        }
                        this.level().playSound(null, this.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 0.6F);
                        this.discard();
                        return;
                    }
                }
                return;
            }

            if (this.jumpscareCooldown > 0) {
                this.jumpscareCooldown--;
                if (this.jumpscareCooldown == 0) {
                    this.setJumpscaring(false);
                }
            }

            if (this.getTarget() instanceof Player player) {
                double dist = this.distanceToSqr(player);

                if (dist < 256.0D) {
                    if (this.soundCooldown > 0) {
                        this.soundCooldown--;
                    } else {
                        this.level().playSound(null, this.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.HOSTILE, 1.5F, 0.5F);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.MINECART_RIDING, SoundSource.HOSTILE, 1.0F, 0.6F);
                        this.soundCooldown = 40 + this.random.nextInt(30);
                    }

                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false));
                }

                if (dist < 100.0D && !this.isJumpscaring() && this.jumpscareCooldown == 0) {
                    this.setJumpscaring(true);
                    this.jumpscareCooldown = 100;
                    this.level().playSound(null, this.blockPosition(), SoundInit.BACTERIA_SCREAM.get(), SoundSource.HOSTILE, 3.0F, 1.0F);
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, false));

                    double radius = 16.0D;
                    java.util.List<net.minecraft.server.level.ServerPlayer> players = this.level().getEntitiesOfClass(
                            net.minecraft.server.level.ServerPlayer.class,
                            this.getBoundingBox().inflate(radius)
                    );
                    for (net.minecraft.server.level.ServerPlayer serverPlayer : players) {
                        com.litvin.backrooms.network.PacketHandler.sendToClient(
                                new com.litvin.backrooms.network.ClientboundJumpscarePacket(),
                                serverPlayer
                        );
                    }
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 28.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.35D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

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

    private PlayState predicate(AnimationState<BacteriaEntity> event) {
        if (this.isJumpscaring()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.bacteria.jumpscare"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.bacteria.walk"));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.bacteria.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
