/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.AnimationState
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package fr.meulti.mbackrooms.entity.custom;

import fr.meulti.mbackrooms.entity.ai.DRAttackGoal;
import fr.meulti.mbackrooms.sound.ModSounds;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DeathRatEntity
extends Monster {
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.m_135353_(DeathRatEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final List<SoundEvent> SOUNDS = Arrays.asList((SoundEvent)ModSounds.SOUND_RAT_1.get(), (SoundEvent)ModSounds.SOUND_RAT_2.get(), (SoundEvent)ModSounds.SOUND_RAT_3.get(), (SoundEvent)ModSounds.SOUND_RAT_4.get(), (SoundEvent)ModSounds.SOUND_RAT_5.get(), (SoundEvent)ModSounds.SOUND_RAT_6.get(), (SoundEvent)ModSounds.SOUND_RAT_7.get(), (SoundEvent)ModSounds.SOUND_RAT_8.get());
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public DeathRatEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().m_5776_()) {
            this.setupAnimationStates();
        }
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

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ATTACKING, (Object)false);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(1, (Goal)new DRAttackGoal((PathfinderMob)this, 1.3, true));
        this.f_21345_.m_25352_(5, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 3.0f));
        this.f_21345_.m_25352_(6, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
        this.f_21346_.m_25352_(2, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.m_21183_().m_22268_(Attributes.f_22276_, 5.0).m_22268_(Attributes.f_22277_, 10.0).m_22268_(Attributes.f_22279_, 0.28).m_22268_(Attributes.f_22285_, (double)0.1f).m_22268_(Attributes.f_22282_, 0.5).m_22268_(Attributes.f_22281_, 3.0);
    }

    @Nullable
    protected SoundEvent m_7515_() {
        return SOUNDS.get(this.f_19796_.m_188503_(SOUNDS.size()));
    }

    @Nullable
    protected SoundEvent m_7975_(DamageSource pDamageSource) {
        return SOUNDS.get(this.f_19796_.m_188503_(SOUNDS.size()));
    }

    @Nullable
    protected SoundEvent m_5592_() {
        return SOUNDS.get(this.f_19796_.m_188503_(SOUNDS.size()));
    }

    public boolean m_21532_() {
        return true;
    }
}
