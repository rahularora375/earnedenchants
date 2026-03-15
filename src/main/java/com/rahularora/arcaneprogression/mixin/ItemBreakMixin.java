package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ItemBreakMixin {

    @Inject(method = "onEquippedItemBroken", at = @At("HEAD"))
    private void onItemBroken(Item item, EquipmentSlot slot, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer serverPlayer) {
            int count = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.TOOLS_BROKEN);
            count++;
            serverPlayer.setAttached(PlayerDataAttachments.TOOLS_BROKEN, count);
            ArcaneProgression.TOOLS_BROKEN_TRIGGER.trigger(serverPlayer, count);
        }
    }
}