package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class WetTravelMixin {

    @Inject(method = "checkMovementStatistics", at = @At("HEAD"))
    private void onMovement(double dx, double dy, double dz, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        int cm = (int) (Math.sqrt(dx * dx + dz * dz) * 100);
        if (cm <= 0) return;

        // Track horizontal distance traveled while in rain (Riptide)
        if (player.level().isRainingAt(player.blockPosition())) {
            int rainTotal = player.getAttachedOrCreate(PlayerDataAttachments.RAIN_TRAVEL) + cm;
            player.setAttached(PlayerDataAttachments.RAIN_TRAVEL, rainTotal);
            ArcaneProgression.RAIN_TRAVEL_TRIGGER.trigger(player, rainTotal / 100);
        }

        // Track horizontal distance traveled underwater (Depth Strider)
        if (player.isUnderWater()) {
            int uwTotal = player.getAttachedOrCreate(PlayerDataAttachments.UNDERWATER_TRAVEL) + cm;
            player.setAttached(PlayerDataAttachments.UNDERWATER_TRAVEL, uwTotal);
            ArcaneProgression.UNDERWATER_TRAVEL_TRIGGER.trigger(player, uwTotal / 100);
        }

        // Track horizontal distance traveled in snowy biomes (Frost Walker)
        if (player.level().getBiome(player.blockPosition()).value()
                .coldEnoughToSnow(player.blockPosition(), player.level().getSeaLevel())) {
            int snowTotal = player.getAttachedOrCreate(PlayerDataAttachments.SNOWY_TRAVEL) + cm;
            player.setAttached(PlayerDataAttachments.SNOWY_TRAVEL, snowTotal);
            ArcaneProgression.SNOWY_TRAVEL_TRIGGER.trigger(player, snowTotal / 100);
        }

        // Track horizontal distance traveled in the Nether (Soul Speed)
        if (player.level().dimension() == Level.NETHER) {
            int netherTotal = player.getAttachedOrCreate(PlayerDataAttachments.NETHER_TRAVEL) + cm;
            player.setAttached(PlayerDataAttachments.NETHER_TRAVEL, netherTotal);
            ArcaneProgression.NETHER_TRAVEL_TRIGGER.trigger(player, netherTotal / 100);
        }

        // Track horizontal distance traveled while sneaking (Swift Sneak)
        if (player.isCrouching()) {
            int sneakTotal = player.getAttachedOrCreate(PlayerDataAttachments.SNEAK_TRAVEL) + cm;
            player.setAttached(PlayerDataAttachments.SNEAK_TRAVEL, sneakTotal);
            ArcaneProgression.SNEAK_TRAVEL_TRIGGER.trigger(player, sneakTotal / 100);
        }
    }
}