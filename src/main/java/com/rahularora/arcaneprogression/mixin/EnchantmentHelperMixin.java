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

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantable;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
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
        Enchantable enchantable = stack.get(DataComponents.ENCHANTABLE);
        int enchantability = enchantable != null ? enchantable.value() : 0;
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
                int[] override = EnchantmentUnlockData.getCostOverride(keyPath, level, enchantability);
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

        // Add treasure enchantments that vanilla excluded (modified cost outside vanilla ranges)
        // but our cost overrides would accept. This fixes e.g. Frost Walker 2 being unreachable
        // because our override min (36) exceeds vanilla's max cost (35).
        Set<String> presentKeys = new HashSet<>();
        for (EnchantmentInstance ei : results) {
            ResourceKey<Enchantment> k = ei.enchantment().unwrapKey().orElse(null);
            if (k != null) presentKeys.add(k.identifier().getPath());
        }

        var registry = player.level().getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        for (String treasureKey : EnchantmentUnlockData.TREASURE_ENCHANTMENT_KEYS) {
            if (presentKeys.contains(treasureKey)) continue;

            var ref = registry.get(Identifier.withDefaultNamespace(treasureKey));
            if (ref.isEmpty()) continue;

            Holder<Enchantment> holder = ref.get();
            if (!holder.value().isSupportedItem(stack)) continue;

            ResourceKey<Enchantment> rk = holder.unwrapKey().orElse(null);
            if (rk == null) continue;

            int maxUnlocked = EnchantmentUnlockData.getMaxUnlockedLevel(player, rk);
            if (maxUnlocked <= 0) continue;

            for (int level = maxUnlocked; level >= holder.value().getMinLevel(); level--) {
                int[] override = EnchantmentUnlockData.getCostOverride(treasureKey, level, enchantability);
                if (override != null && cost >= override[0] && cost <= override[1]) {
                    results.add(new EnchantmentInstance(holder, level));
                    break;
                }
            }
        }
    }
}
