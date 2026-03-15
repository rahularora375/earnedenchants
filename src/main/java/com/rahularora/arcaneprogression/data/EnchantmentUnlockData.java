package com.rahularora.arcaneprogression.data;

import com.rahularora.arcaneprogression.ArcaneProgression;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantmentUnlockData {

    // ThreadLocal to pass player context from EnchantmentMenu into EnchantmentHelper
    public static final ThreadLocal<ServerPlayer> ENCHANTING_PLAYER = new ThreadLocal<>();

    public record UnlockInfo(String advancementName, int maxLevel, boolean isTreasure) {}

    private static final Map<String, UnlockInfo> UNLOCK_MAP = new HashMap<>();

    // Custom cost overrides for final-tier enchantments that are unreachable or too easy in vanilla.
    // Key: "enchantmentKey:level", Value: [minCost, maxCost]
    // Tuned so these only appear near the max modified cost on diamond/netherite gear.
    private static final Map<String, int[]> COST_OVERRIDES = new HashMap<>();

    public static final Set<String> TREASURE_ENCHANTMENT_KEYS = Set.of(
            "frost_walker", "soul_speed", "swift_sneak", "wind_burst", "mending"
    );

    // Number of trackable enchantment advancements (excludes root, category headers, mending)
    public static final int MENDING_ADVANCEMENT_COUNT = 125;

    static {
        // Cost overrides — CORRECTED: diamond max modified = 40, bow/crossbow/trident max = 36, mace/netherite max = 43
        // Diamond ench=10: P(>=37)≈7%, P(>=38)≈2.7%, P(>=39)≈0.8%, P(>=40)≈0.1%
        // Bow ench=1: P(>=35)≈3%
        // Mace ench=15: P(>=37)≈20%, P(>=38)≈10%, P(>=39)≈4.7%, P(>=40)≈1.9%

        // Non-treasure final tiers
        COST_OVERRIDES.put("efficiency:5", new int[]{36, 200});           // ~15% on diamond (vanilla 41 unreachable)
        COST_OVERRIDES.put("sharpness:5", new int[]{36, 200});            // ~15% on diamond
        COST_OVERRIDES.put("power:5", new int[]{34, 200});                // ~11% on bow
        COST_OVERRIDES.put("bane_of_arthropods:5", new int[]{36, 200});   // ~15% on diamond
        COST_OVERRIDES.put("quick_charge:3", new int[]{34, 200});         // ~11% on crossbow
        COST_OVERRIDES.put("thorns:3", new int[]{36, 200});               // ~15% on diamond armor
        COST_OVERRIDES.put("breach:4", new int[]{36, 200});               // ~30% on mace (vanilla 42 was ~0.1%)

        // Frost Walker (2 tiers)
        COST_OVERRIDES.put("frost_walker:1", new int[]{34, 200});         // ~53% on diamond
        COST_OVERRIDES.put("frost_walker:2", new int[]{36, 200});         // ~15% on diamond

        // Soul Speed (3 tiers)
        COST_OVERRIDES.put("soul_speed:1", new int[]{33, 200});           // ~78% on diamond
        COST_OVERRIDES.put("soul_speed:2", new int[]{35, 200});           // ~31% on diamond
        COST_OVERRIDES.put("soul_speed:3", new int[]{36, 200});           // ~15% on diamond

        // Swift Sneak (3 tiers)
        COST_OVERRIDES.put("swift_sneak:1", new int[]{34, 200});          // ~53% on diamond
        COST_OVERRIDES.put("swift_sneak:2", new int[]{35, 200});          // ~31% on diamond
        COST_OVERRIDES.put("swift_sneak:3", new int[]{36, 200});          // ~15% on diamond

        // Wind Burst (3 tiers) — mace only (ench=15)
        COST_OVERRIDES.put("wind_burst:1", new int[]{34, 200});           // ~70% on mace
        COST_OVERRIDES.put("wind_burst:2", new int[]{36, 200});           // ~30% on mace
        COST_OVERRIDES.put("wind_burst:3", new int[]{38, 200});           // ~10% on mace

        // Mending — the rarest
        COST_OVERRIDES.put("mending:1", new int[]{36, 200});              // ~15% on diamond, ~30% on mace/netherite


        // Tool enchantments
        register("efficiency", "efficiency", 5, false);
        register("fortune", "fortune", 3, false);
        register("silk_touch", "silk_touch", 1, false);
        register("unbreaking", "unbreaking", 3, false);

        // Fishing enchantments
        register("lure", "lure", 3, false);
        register("luck_of_the_sea", "luck_of_the_sea", 3, false);

        // Melee enchantments
        register("sharpness", "sharpness", 5, false);
        register("smite", "smite", 5, false);
        register("bane_of_arthropods", "bane_of_arthropods", 5, false);
        register("knockback", "knockback", 2, false);
        register("fire_aspect", "fire_aspect", 2, false);
        register("looting", "looting", 3, false);
        register("sweeping_edge", "sweeping_edge", 3, false);
        register("lunge", "lunge", 3, false);

        // Bow enchantments
        register("power", "power", 5, false);
        register("punch", "punch", 2, false);
        register("flame", "flame", 1, false);
        register("infinity", "infinity", 1, false);

        // Crossbow enchantments
        register("quick_charge", "quick_charge", 3, false);
        register("multishot", "multishot", 1, false);
        register("piercing", "piercing", 4, false);

        // Trident enchantments
        register("loyalty", "loyalty", 3, false);
        register("riptide", "riptide", 3, false);
        register("channeling", "channeling", 1, false);
        register("impaling", "impaling", 5, false);

        // Mace enchantments
        register("breach", "breach", 4, false);
        register("density", "density", 5, false);

        // Armor enchantments
        register("protection", "protection", 4, false);
        register("fire_protection", "fire_protection", 4, false);
        register("blast_protection", "blast_protection", 4, false);
        register("projectile_protection", "projectile_protection", 4, false);
        register("feather_falling", "feather_falling", 4, false);
        register("thorns", "thorns", 3, false);
        register("aqua_affinity", "aqua_affinity", 1, false);
        register("respiration", "respiration", 3, false);
        register("depth_strider", "depth_strider", 3, false);

        // Treasure enchantments
        register("frost_walker", "frost_walker", 2, true);
        register("soul_speed", "soul_speed", 3, true);
        register("swift_sneak", "swift_sneak", 3, true);
        register("wind_burst", "wind_burst", 3, true);
        register("mending", "mending", 1, true);
    }

    private static void register(String enchantmentKey, String advancementName, int maxLevel, boolean isTreasure) {
        UNLOCK_MAP.put(enchantmentKey, new UnlockInfo(advancementName, maxLevel, isTreasure));
    }

    /**
     * Returns custom [minCost, maxCost] override for an enchantment at a specific level, or null if vanilla.
     */
    public static int[] getCostOverride(String enchantmentPath, int level) {
        return COST_OVERRIDES.get(enchantmentPath + ":" + level);
    }

    /**
     * Returns the max unlocked level for the given enchantment, or 0 if fully locked.
     * Returns Integer.MAX_VALUE for enchantments not managed by this mod (e.g., from other mods).
     */
    public static int getMaxUnlockedLevel(ServerPlayer player, ResourceKey<Enchantment> enchantmentKey) {
        String path = enchantmentKey.identifier().getPath();
        UnlockInfo info = UNLOCK_MAP.get(path);
        if (info == null) {
            return Integer.MAX_VALUE; // Not managed by us — let it through
        }

        var advancementManager = player.level().getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();

        // Check from highest tier downward
        for (int level = info.maxLevel(); level >= 1; level--) {
            String advId = info.advancementName() + (info.maxLevel() == 1 ? "" : "_" + level);
            var holder = advancementManager.get(
                    Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "tool/" + advId)
            );
            if (holder != null && playerAdvancements.getOrStartProgress(holder).isDone()) {
                return level;
            }
        }

        return 0; // Fully locked
    }

    /**
     * Returns holders for treasure enchantments the player has unlocked (any tier).
     */
    public static List<Holder<Enchantment>> getUnlockedTreasureHolders(ServerPlayer player, Registry<Enchantment> registry) {
        List<Holder<Enchantment>> result = new ArrayList<>();
        for (String key : TREASURE_ENCHANTMENT_KEYS) {
            var ref = registry.get(Identifier.withDefaultNamespace(key));
            if (ref.isPresent()) {
                ResourceKey<Enchantment> rk = ref.get().unwrapKey().orElse(null);
                if (rk != null && getMaxUnlockedLevel(player, rk) > 0) {
                    result.add(ref.get());
                }
            }
        }
        return result;
    }
}
