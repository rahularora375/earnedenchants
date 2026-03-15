package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ProjectileHitMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void onProjectileHit(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof ServerPlayer player
                && source.getDirectEntity() instanceof AbstractArrow) {
            LivingEntity self = (LivingEntity) (Object) this;

            // Ranged damage (Power)
            int rangedDamage = player.getAttachedOrCreate(PlayerDataAttachments.RANGED_DAMAGE);
            rangedDamage += (int) amount;
            player.setAttached(PlayerDataAttachments.RANGED_DAMAGE, rangedDamage);
            ArcaneProgression.RANGED_DAMAGE_TRIGGER.trigger(player, rangedDamage);

            // Long-range hits (Punch) — 24+ blocks away
            if (player.distanceTo(self) >= 24.0) {
                int longRangeHits = player.getAttachedOrCreate(PlayerDataAttachments.LONG_RANGE_HITS);
                longRangeHits++;
                player.setAttached(PlayerDataAttachments.LONG_RANGE_HITS, longRangeHits);
                ArcaneProgression.LONG_RANGE_HITS_TRIGGER.trigger(player, longRangeHits);
            }
        }
    }
}