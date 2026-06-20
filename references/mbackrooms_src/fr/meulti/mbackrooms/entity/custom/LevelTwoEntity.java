/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.AnimationState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.MoveToBlockGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.network.PacketDistributor
 */
package fr.meulti.mbackrooms.entity.custom;

import fr.meulti.mbackrooms.BackroomsMod;
import fr.meulti.mbackrooms.entity.ai.MoveOnXAxisGoal;
import fr.meulti.mbackrooms.event.entity.EntityDeathEvent;
import fr.meulti.mbackrooms.networking.JumpScarePacket;
import fr.meulti.mbackrooms.networking.PacketsHandler;
import fr.meulti.mbackrooms.sound.ModSounds;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.PacketDistributor;

public class LevelTwoEntity
extends Monster {
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.m_135353_(LevelTwoEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;
    private Vec3 direction = this.getRandomDirection();
    private static final int DESPAWN_TIME_TICKS = 380;
    private int despawnTimer = 0;
    private int attackCooldown = 0;
    private static final List<SoundEvent> PURSUIT_SOUNDS = Arrays.asList((SoundEvent)ModSounds.LEVEL_TWO_ENTITY_AMBIANT_1.get(), (SoundEvent)ModSounds.LEVEL_TWO_ENTITY_AMBIANT_2.get(), (SoundEvent)ModSounds.LEVEL_TWO_ENTITY_AMBIANT_3.get(), (SoundEvent)ModSounds.LEVEL_TWO_ENTITY_AMBIANT_4.get(), (SoundEvent)ModSounds.LEVEL_TWO_ENTITY_AMBIANT_5.get(), (SoundEvent)ModSounds.LEVEL_TWO_ENTITY_AMBIANT_6.get());

    public LevelTwoEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void m_8119_() {
        super.m_8119_();
        if (!this.m_9236_().m_5776_()) {
            this.m_6034_(this.m_20185_(), 41.0, 13.5);
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
            this.m_9236_().m_6443_(Player.class, this.m_20191_().m_82400_(5.0), player -> !player.m_7500_() && !player.m_5833_() && player.m_6084_()).forEach(player -> player.m_7292_(new MobEffectInstance(MobEffects.f_19610_, 20, 0, false, false, false)));
            this.m_9236_().m_6443_(Player.class, this.m_20191_().m_82400_(1.5), player -> !player.m_7500_() && !player.m_5833_() && player.m_6084_()).forEach(player -> {
                if (this.attackCooldown <= 0) {
                    float healthMalus;
                    float halfHealth = player.m_21233_() / 2.0f;
                    if (player.m_21223_() < halfHealth) {
                        healthMalus = 100.0f;
                        if (!player.m_9236_().f_46443_) {
                            JumpScarePacket jumpscarePacket = new JumpScarePacket(BackroomsMod.getModResource("textures/jumpscare/level_two_entity_jumpscare.png"), ModSounds.SMILER_CHASING.getId(), false, 60);
                            PacketsHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), (Object)jumpscarePacket);
                        }
                    } else {
                        healthMalus = halfHealth + 1.0f;
                    }
                    player.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), healthMalus);
                    double knockbackStrength = 5.0;
                    double directionX = -1.0;
                    player.m_147240_(knockbackStrength, directionX, 0.0);
                    this.attackCooldown = 40;
                }
            });
            if (this.f_19797_ % 50 == 0) {
                this.playRandomPursuitSound();
            }
        }
        if (this.m_9236_().m_5776_()) {
            this.setupAnimationStates();
        }
    }

    private void playRandomPursuitSound() {
        SoundEvent randomSound = PURSUIT_SOUNDS.get(this.f_19796_.m_188503_(PURSUIT_SOUNDS.size()));
        this.m_9236_().m_5594_(null, this.m_20183_(), randomSound, SoundSource.HOSTILE, 5.0f, 1.0f);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ATTACKING, (Object)false);
    }

    public boolean m_6063_() {
        return false;
    }

    public boolean m_5829_() {
        return false;
    }

    public boolean m_7337_(Entity p_20303_) {
        return false;
    }

    protected void m_7324_(Entity pEntity) {
    }

    public boolean m_20039_(BlockPos pPos, BlockState pState) {
        return false;
    }

    public boolean m_5830_() {
        return false;
    }

    protected void m_20101_() {
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(2, (Goal)new MoveOnXAxisGoal(this, 0.51));
    }

    public void m_6667_(DamageSource cause) {
        if (!this.m_9236_().m_5776_()) {
            this.m_142687_(Entity.RemovalReason.KILLED);
            MinecraftForge.EVENT_BUS.post((Event)new EntityDeathEvent((Entity)this));
            this.m_142687_(Entity.RemovalReason.KILLED);
        }
        super.m_6667_(cause);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, 200.0).m_22268_(Attributes.f_22277_, 50.0).m_22268_(Attributes.f_22279_, 0.51).m_22268_(Attributes.f_22285_, (double)0.1f).m_22268_(Attributes.f_22282_, 0.5).m_22268_(Attributes.f_22281_, 20.0);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.f_19796_.m_188503_(40) + 80;
            this.idleAnimationState.m_216977_(this.f_19797_);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    protected void m_267689_(float pPartialTick) {
        float f = this.m_20089_() == Pose.STANDING ? Math.min(pPartialTick * 6.0f, 1.0f) : 0.0f;
        this.f_267362_.m_267566_(f, 0.2f);
    }

    public boolean m_21532_() {
        return true;
    }

    private Vec3 getRandomDirection() {
        Random random = new Random();
        double angle = random.nextDouble() * 2.0 * Math.PI;
        return new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
    }

    public void setMovementDirection(Vec3 direction) {
        this.direction = direction.m_82541_();
    }

    public Vec3 getMovementDirection() {
        return this.direction;
    }

    private static class MoveInDirectionGoal
    extends MoveToBlockGoal {
        private final LevelTwoEntity entity;
        private boolean isStuck = false;

        public MoveInDirectionGoal(LevelTwoEntity pMob, double pSpeedModifier) {
            super((PathfinderMob)pMob, pSpeedModifier, 24);
            this.entity = pMob;
        }

        public boolean m_8036_() {
            if (this.entity.m_5448_() != null) {
                return false;
            }
            if (this.entity.m_21573_().m_26571_() && !this.isStuck) {
                this.entity.setMovementDirection(this.entity.getRandomDirection());
            }
            return super.m_8036_();
        }

        public boolean m_8045_() {
            return !this.isStuck && super.m_8045_();
        }

        public void m_8056_() {
            super.m_8056_();
            this.entity.m_21573_().m_26519_((double)this.f_25602_.m_123341_() + 0.5, (double)this.f_25602_.m_123342_(), (double)this.f_25602_.m_123343_() + 0.5, this.f_25599_);
            this.isStuck = false;
        }

        public void m_8041_() {
            super.m_8041_();
            this.isStuck = false;
        }

        public void m_8037_() {
            super.m_8037_();
            if (this.entity.m_21573_().m_26571_()) {
                this.isStuck = true;
            }
        }

        protected boolean m_6465_(LevelReader pLevel, BlockPos pPos) {
            BlockPos destination = this.entity.m_20183_().m_7918_((int)(this.entity.getMovementDirection().f_82479_ * 24.0), 0, (int)(this.entity.getMovementDirection().f_82481_ * 24.0));
            BlockState blockState = this.entity.m_9236_().m_8055_(destination);
            return blockState.m_60795_();
        }
    }
}
