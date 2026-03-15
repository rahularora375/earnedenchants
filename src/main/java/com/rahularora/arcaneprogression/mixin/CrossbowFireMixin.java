package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class CrossbowFireMixin {

    @Inject(method = "performShooting", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/stats/Stat;)V"))
    private void onCrossbowFired(Level level, LivingEntity entity, InteractionHand hand,
                                  ItemStack stack, float f1, float f2,
                                  LivingEntity target, CallbackInfo ci) {
        if (entity instanceof ServerPlayer player) {
            int fired = player.getAttachedOrCreate(PlayerDataAttachments.CROSSBOW_FIRED);
            fired++;
            player.setAttached(PlayerDataAttachments.CROSSBOW_FIRED, fired);
            ArcaneProgression.CROSSBOW_FIRED_TRIGGER.trigger(player, fired);
        }
    }
}