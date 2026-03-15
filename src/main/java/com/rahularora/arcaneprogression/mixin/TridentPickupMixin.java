package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrownTrident.class)
public class TridentPickupMixin {

    @Inject(method = "tryPickup", at = @At("RETURN"))
    private void onTridentPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        // Only count manual ground retrievals, not loyalty returns
        ThrownTrident trident = (ThrownTrident) (Object) this;
        if (trident.noPhysics) return;

        int count = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.TRIDENT_RETRIEVALS);
        count++;
        serverPlayer.setAttached(PlayerDataAttachments.TRIDENT_RETRIEVALS, count);
        ArcaneProgression.TRIDENT_RETRIEVALS_TRIGGER.trigger(serverPlayer, count);
    }
}