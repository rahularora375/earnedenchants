package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class KnockbackFallMixin {

    @Shadow public abstract Player getLastHurtByPlayer();
    @Shadow public abstract int getLastHurtByPlayerMemoryTime();

    @Inject(method = "causeFallDamage", at = @At("HEAD"))
    private void onMobFall(double fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        // Only count falls > 3 blocks (vanilla safe fall distance) caused by a player's hit
        if (fallDistance > 3.0 && getLastHurtByPlayerMemoryTime() > 0) {
            Player player = getLastHurtByPlayer();
            if (player instanceof ServerPlayer serverPlayer) {
                int accumulated = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.KNOCKBACK_FALL_DISTANCE);
                accumulated += (int) fallDistance;
                serverPlayer.setAttached(PlayerDataAttachments.KNOCKBACK_FALL_DISTANCE, accumulated);
                ArcaneProgression.KNOCKBACK_FALL_TRIGGER.trigger(serverPlayer, accumulated);
            }
        }
    }
}