package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementAwardMixin {

    @Shadow
    private ServerPlayer player;

    // Advancements that don't count toward Mending (root, category headers, mending itself)
    @Unique
    private static final Set<String> EXCLUDED_IDS = Set.of(
            "earnedenchants:tool/root",
            "earnedenchants:tool/tool_cat",
            "earnedenchants:tool/fishing_cat",
            "earnedenchants:tool/melee_cat",
            "earnedenchants:tool/bow_cat",
            "earnedenchants:tool/crossbow_cat",
            "earnedenchants:tool/trident_cat",
            "earnedenchants:tool/mace_cat",
            "earnedenchants:tool/armor_cat",
            "earnedenchants:tool/treasure_cat",
            "earnedenchants:tool/mending"
    );

    @Shadow
    public abstract net.minecraft.advancements.AdvancementProgress getOrStartProgress(AdvancementHolder holder);

    @Inject(method = "award", at = @At("RETURN"))
    private void onAdvancementAwarded(AdvancementHolder holder, String criterionKey,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        String id = holder.id().toString();
        // Only track arcane_progression advancements, excluding root/categories/mending
        if (!id.startsWith("earnedenchants:")) return;
        if (EXCLUDED_IDS.contains(id)) return;
        if (!getOrStartProgress(holder).isDone()) return;

        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        ServerPlayer capturedPlayer = player;
        server.execute(() -> {
            if (capturedPlayer.hasDisconnected()) return;

            PlayerAdvancements advancements = capturedPlayer.getAdvancements();
            var manager = server.getAdvancements();

            // Count all completed arcane_progression advancements (excluding special ones)
            int count = 0;
            for (AdvancementHolder adv : manager.getAllAdvancements()) {
                String advId = adv.id().toString();
                if (!advId.startsWith("earnedenchants:")) continue;
                if (EXCLUDED_IDS.contains(advId)) continue;
                if (advancements.getOrStartProgress(adv).isDone()) {
                    count++;
                }
            }

            capturedPlayer.setAttached(PlayerDataAttachments.ENCHANTMENTS_UNLOCKED, count);
            ArcaneProgression.ENCHANTMENTS_UNLOCKED_TRIGGER.trigger(capturedPlayer, count);
        });
    }
}
