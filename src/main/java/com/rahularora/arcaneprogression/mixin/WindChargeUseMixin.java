package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WindChargeItem.class)
public class WindChargeUseMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void onWindChargeUse(Level level, Player player, InteractionHand hand,
                                  CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            int count = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.WIND_CHARGES_USED) + 1;
            serverPlayer.setAttached(PlayerDataAttachments.WIND_CHARGES_USED, count);
            ArcaneProgression.WIND_CHARGES_USED_TRIGGER.trigger(serverPlayer, count);
        }
    }
}
