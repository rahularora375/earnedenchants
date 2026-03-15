package com.rahularora.arcaneprogression.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public class FishingMixin {

    @Inject(method = "retrieve",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
                    ordinal = 0))
    private void onCaughtItem(ItemStack rodStack, CallbackInfoReturnable<Integer> cir, @Local ItemEntity itemEntity) {
        FishingHook hook = (FishingHook) (Object) this;
        Player player = hook.getPlayerOwner();
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack caughtItem = itemEntity.getItem();

            // Track total catches (for Lure)
            int catchCount = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.FISH_CAUGHT);
            catchCount++;
            serverPlayer.setAttached(PlayerDataAttachments.FISH_CAUGHT, catchCount);
            ArcaneProgression.FISH_CAUGHT_TRIGGER.trigger(serverPlayer, catchCount);

            // Track treasure catches (for Luck of the Sea)
            if (isTreasureCatch(caughtItem)) {
                int treasureCount = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.TREASURE_CAUGHT);
                treasureCount++;
                serverPlayer.setAttached(PlayerDataAttachments.TREASURE_CAUGHT, treasureCount);
                ArcaneProgression.TREASURE_CAUGHT_TRIGGER.trigger(serverPlayer, treasureCount);
            }
        }
    }

    private static boolean isTreasureCatch(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.BOW || item == Items.ENCHANTED_BOOK || item == Items.FISHING_ROD
                || item == Items.NAME_TAG || item == Items.NAUTILUS_SHELL || item == Items.SADDLE;
    }
}