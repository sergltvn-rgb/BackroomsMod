package com.litvin.backrooms.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Поведение Смайлера в стиле «плачущего ангела»:
 *  - замирает, когда игрок смотрит на него;
 *  - быстро подкрадывается и телепортируется за спину, пока на него не смотрят;
 *  - атакует вблизи.
 */
public class SmilerStalkGoal extends Goal {
    private final SmilerEntity smiler;
    private Player target;
    private int teleportCooldown = 0;
    private int attackCooldown = 0;

    public SmilerStalkGoal(SmilerEntity smiler) {
        this.smiler = smiler;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private static boolean validTarget(Player p) {
        if (p == null || !p.isAlive() || p.isCreative() || p.isSpectator()) return false;
        return p.getCapability(com.litvin.backrooms.capability.SanityProvider.SANITY).map(s -> !s.isHidden()).orElse(true);
    }

    @Override
    public boolean canUse() {
        LivingEntity t = smiler.getTarget();
        if (t instanceof Player p && validTarget(p)) {
            this.target = p;
            return true;
        }
        Player p = smiler.level().getNearestPlayer(smiler, 32.0D);
        if (validTarget(p)) {
            this.target = p;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return validTarget(target) && smiler.distanceToSqr(target) < 48.0D * 48.0D;
    }

    @Override
    public void stop() {
        this.target = null;
        smiler.getNavigation().stop();
        smiler.setFrozen(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null) return;
        if (teleportCooldown > 0) teleportCooldown--;
        if (attackCooldown > 0) attackCooldown--;

        smiler.getLookControl().setLookAt(target, 30.0F, 30.0F);

        boolean lookedAt = isLookedAtBy(target);
        double distSqr = smiler.distanceToSqr(target);

        // Пока на него смотрят — замирает как статуя
        if (lookedAt) {
            smiler.setFrozen(true);
            smiler.getNavigation().stop();
            smiler.setDeltaMovement(0, smiler.getDeltaMovement().y, 0);
            return;
        }

        smiler.setFrozen(false);

        // Атака вблизи
        if (distSqr < 4.0D) {
            smiler.getNavigation().stop();
            if (attackCooldown == 0) {
                smiler.doHurtTarget(target);
                attackCooldown = 20;
            }
            return;
        }

        // Телепорт за спину на средней дистанции
        if (teleportCooldown == 0 && distSqr > 36.0D && distSqr < 28.0D * 28.0D) {
            if (tryTeleportBehind(target)) {
                teleportCooldown = 100 + smiler.getRandom().nextInt(100);
                return;
            }
        }

        // Быстрое приближение, пока не видят
        smiler.getNavigation().moveTo(target, 1.45D);
    }

    private boolean isLookedAtBy(Player player) {
        Vec3 view = player.getViewVector(1.0F).normalize();
        Vec3 toSmiler = new Vec3(
                smiler.getX() - player.getX(),
                smiler.getEyeY() - player.getEyeY(),
                smiler.getZ() - player.getZ()
        ).normalize();
        double dot = view.dot(toSmiler);
        // Конус около 50° + прямая видимость
        return dot > 0.6D && player.hasLineOfSight(smiler);
    }

    private boolean tryTeleportBehind(Player player) {
        Vec3 back = player.getViewVector(1.0F).normalize().scale(-1.0D);
        double dist = 3.0D + smiler.getRandom().nextDouble() * 2.0D;
        double tx = player.getX() + back.x * dist;
        double tz = player.getZ() + back.z * dist;
        double ty = player.getY();

        BlockPos base = BlockPos.containing(tx, ty, tz);
        for (int dy = 1; dy >= -3; dy--) {
            BlockPos check = base.offset(0, dy, 0);
            if (smiler.level().isEmptyBlock(check)
                    && smiler.level().isEmptyBlock(check.above())
                    && !smiler.level().isEmptyBlock(check.below())
                    && smiler.level().getMaxLocalRawBrightness(check) <= 5) {
                smiler.teleportTo(check.getX() + 0.5D, check.getY(), check.getZ() + 0.5D);
                smiler.level().playSound(null, smiler.blockPosition(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.4F, 0.5F);
                return true;
            }
        }
        return false;
    }
}
