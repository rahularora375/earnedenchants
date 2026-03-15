package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class SweepAttackMixin {

    @Inject(method = "doSweepAttack", at = @At("HEAD"))
    private void onSweepAttack(Entity target, float damage, DamageSource source, float knockback, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer serverPlayer)) return;

        // Count nearby entities that will be hit by sweep (same filters as vanilla)
        List<LivingEntity> nearby = self.level().getEntitiesOfClass(LivingEntity.class,
                target.getBoundingBox().inflate(1.0, 0.25, 1.0));

        int sweepHits = 0;
        for (LivingEntity entity : nearby) {
            if (entity == self || entity == target) continue;
            if (self.isAlliedTo(entity)) continue;
            if (entity instanceof ArmorStand armorStand && armorStand.isMarker()) continue;
            if (self.distanceToSqr(entity) >= 9.0) continue;
            sweepHits++;
        }

        // sweepHits = additional mobs hit. Total = 1 (primary target) + sweepHits.
        // Condition: hit 3+ mobs in a single sweep → sweepHits >= 2
        if (sweepHits >= 2) {
            int count = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.SWEEP_MULTI_HITS);
            count++;
            serverPlayer.setAttached(PlayerDataAttachments.SWEEP_MULTI_HITS, count);
            ArcaneProgression.SWEEP_MULTI_HIT_TRIGGER.trigger(serverPlayer, count);
        }
    }
}