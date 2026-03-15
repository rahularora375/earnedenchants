package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.data.EnchantmentUnlockData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Cap enchantment levels and apply custom cost overrides in getAvailableEnchantmentResults.
    // For each result, scans from maxUnlocked down using either custom or vanilla cost ranges.
    // This handles both capping (vanilla level too high) and upgrading (custom override allows higher).
    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void arcane$applyUnlockOverrides(int cost, ItemStack stack, Stream<Holder<Enchantment>> stream,
                                                     CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        ServerPlayer player = EnchantmentUnlockData.ENCHANTING_PLAYER.get();
        if (player == null) return;

        List<EnchantmentInstance> results = cir.getReturnValue();
        ListIterator<EnchantmentInstance> it = results.listIterator();

        while (it.hasNext()) {
            EnchantmentInstance instance = it.next();
            ResourceKey<Enchantment> key = instance.enchantment().unwrapKey().orElse(null);
            if (key == null) continue;

            String keyPath = key.identifier().getPath();
            int maxUnlocked = EnchantmentUnlockData.getMaxUnlockedLevel(player, key);

            if (maxUnlocked == Integer.MAX_VALUE) continue;
            if (maxUnlocked == 0) { it.remove(); continue; }

            Enchantment enchantment = instance.enchantment().value();
            boolean found = false;
            for (int level = maxUnlocked; level >= enchantment.getMinLevel(); level--) {
                int[] override = EnchantmentUnlockData.getCostOverride(keyPath, level);
                boolean valid;
                if (override != null) {
                    valid = cost >= override[0] && cost <= override[1];
                } else {
                    valid = cost >= enchantment.getMinCost(level) && cost <= enchantment.getMaxCost(level);
                }
                if (valid) {
                    it.set(new EnchantmentInstance(instance.enchantment(), level));
                    found = true;
                    break;
                }
            }
            if (!found) {
                it.remove();
            }
        }
    }
}
