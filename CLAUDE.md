# Arcane Progression

## Project Overview

A Fabric mod for Minecraft Java Edition 1.21.11 ("Mounts of Mayhem") that adds an enchantment unlock progression system. Players must complete specific in-game tasks (mining blocks, killing mobs, taking damage, etc.) to unlock enchantments via a custom advancement tree. The advancements are viewable through the Better Advancements mod (client-side UI mod, no integration code needed — it reads vanilla advancement data automatically).

The full list of enchantments and their unlock conditions is in `enchantment-unlocks-raw.md` in the Claude projects directory.
A detailed technical reference covering vanilla stat analysis, custom tracking requirements, and performance strategy is in `enchantment-unlocks-mod-reference.md` in the Claude projects directory.
(Path: `C:\Users\Rahul Arora\.claude\projects\E--arcane-progression-template-1-21-11\`)

## Current State

**Implemented & working:**
- **Tool enchantments:** Efficiency I–V, Fortune I–III, Silk Touch, Unbreaking I–III
- **Fishing enchantments:** Lure I–III, Luck of the Sea I–III
- **Melee enchantments:** Sharpness I–V, Smite I–V, Bane of Arthropods I–V, Knockback I–II, Fire Aspect I–II, Looting I–III, Sweeping Edge I–III, Lunge I–III
- **Bow enchantments:** Power I–V, Punch I–II, Flame, Infinity
- **Crossbow enchantments:** Quick Charge I–III, Multishot, Piercing I–IV
- **Trident enchantments:** Loyalty I–III, Riptide I–III, Channeling, Impaling I–V
- **Mace enchantments:** Breach I–IV, Density I–V
- **Armor enchantments:** Protection I–IV, Fire Protection I–IV, Blast Protection I–IV, Projectile Protection I–IV, Feather Falling I–IV, Thorns I–III, Aqua Affinity, Respiration I–III, Depth Strider I–III
- **Treasure enchantments:** Frost Walker I–II, Soul Speed I–III, Swift Sneak I–III, Wind Burst I–III
- **Reward chain:** Unbreaking III Book (30 advancements), Efficiency V Book (60), Fortune III Book (70), Mending Book (125) — gives enchanted books via loot table rewards
- **Total:** 138 generated advancements + 1 hand-written root = 139 advancements
- All in a single "Arcane Progression" tab with **category headers** (Tool, Fishing, Melee, Bow, Crossbow, Trident, Mace, Armor, Treasure)
- Category headers are auto-granted advancements (InventoryChangeTrigger, no toast/chat) that group chains visually
- Progress display via sub-criteria (Better Advancements shows "N/M")
- Visibility mixin forces all our advancements visible in the tree
- Better Advancements `criteriaDetail` set to `"Off"`
- **Enchanting table respects unlock progress** — locked enchantments don't appear, per-tier caps validated against vanilla cost ranges, treasure enchantments added when unlocked
- **Custom cost overrides** for 20 enchantment:level combos — makes unreachable max-tier enchants obtainable (~11-15% on diamond)
- **Mending** is NOT available in the enchanting table — it is earned as a book reward for completing all 125 enchantment advancements
- **Easy Magic compatible** (mixin fires on inherited method, no dependency needed)

**All enchantment categories complete.** Curses were dropped from scope.

## Target Platform

- **Minecraft:** 1.21.11 (Mounts of Mayhem, released Dec 9, 2025)
- **Mod Loader:** Fabric (Loom 1.15, Fabric API 0.141.3)
- **Mappings:** Mojang Mappings (NOT Yarn — Yarn is being sunset after 1.21.11)
- **Java:** 21
- **Companion Mod:** Better Advancements v0.4.8.51 (Fabric 1.21.11, client-side only, installed in `run/mods/`)

## Architecture

### Package Structure
```
src/main/java/com/rahularora/arcaneprogression/
├── ArcaneProgression.java           # Mod initializer — registers triggers, attachments, events
├── criteria/                         # Custom criterion trigger classes
│   ├── BlocksMinedTrigger.java      # Fires when player's total blocks mined >= threshold
│   └── CountReachedTrigger.java     # Reusable trigger: fires when any counter >= threshold
├── data/                             # Data attachment type definitions
│   ├── PlayerDataAttachments.java   # Persistent per-player counters (35 attachments)
│   └── EnchantmentUnlockData.java   # Static map of enchantment→advancement + unlock level lookup + ThreadLocal bridge
├── events/                           # Fabric event listeners
│   ├── BlockBreakHandler.java       # Tracks blocks mined, diamonds mined, unique block types
│   └── MobKillHandler.java          # Tracks mob kills, undead/arthropod/fire/spear/ranged/crossbow/thunderstorm/aquatic kills, unique species
└── mixin/                            # Mixin classes
    ├── AdvancementVisibilityMixin.java  # Forces our advancements visible in the tree
    ├── ItemBreakMixin.java              # Tracks tool/weapon/armor breaking via durability
    ├── FishingMixin.java                # Tracks fishing catches + treasure items
    ├── KnockbackFallMixin.java          # Tracks mob fall distance attributed to player hits
    ├── SweepAttackMixin.java            # Tracks sweep attacks hitting 3+ mobs
    ├── ProjectileHitMixin.java          # Tracks ranged damage + long-range hits via LivingEntity.hurtServer
    ├── BowFireMixin.java                # Tracks arrows fired via BowItem.releaseUsing
    ├── CrossbowFireMixin.java           # Tracks crossbow fires via CrossbowItem.performShooting
    ├── TridentPickupMixin.java          # Tracks trident ground retrievals via ThrownTrident.tryPickup
    ├── WetTravelMixin.java              # Tracks distance in rain, underwater, snowy biomes, Nether, sneaking via ServerPlayer.checkMovementStatistics
    ├── MeleeHitMixin.java               # Tracks armored mob hits (Breach) + mace smash damage (Density) via LivingEntity.hurtServer
    ├── DamageTakenMixin.java            # Tracks damage taken by player (Protection, Fire/Blast/Projectile Prot) via LivingEntity.hurtServer
    ├── WindChargeUseMixin.java          # Tracks wind charge uses via WindChargeItem.use()
    ├── AdvancementAwardMixin.java       # Tracks final-tier advancement completions for Mending via PlayerAdvancements.award()
    ├── EnchantingTableMixin.java        # Filters locked enchantments + adds treasure in EnchantmentMenu.getEnchantmentList()
    └── EnchantmentHelperMixin.java      # Caps enchantment levels with cost-range validation in EnchantmentHelper.getAvailableEnchantmentResults()

src/client/java/com/rahularora/arcaneprogression/
├── ArcaneProgressionClient.java      # Client entrypoint (empty for now)
├── ArcaneProgressionDataGenerator.java # Datagen entrypoint
└── datagen/
    └── AdvancementProvider.java      # Generates all advancement JSONs with sub-criteria
```

### Trigger & Attachment Registry

| Trigger ID | Class | Attachment | Type | Used By |
|---|---|---|---|---|
| `blocks_mined` | `BlocksMinedTrigger` | `blocks_mined` | `Integer` | Efficiency I–V |
| `diamonds_mined` | `CountReachedTrigger` | `diamonds_mined` | `Integer` | Fortune I–III |
| `unique_blocks_mined` | `CountReachedTrigger` | `unique_blocks_mined` | `List<String>` | Silk Touch |
| `tools_broken` | `CountReachedTrigger` | `tools_broken` | `Integer` | Unbreaking I–III |
| `fish_caught` | `CountReachedTrigger` | `fish_caught` | `Integer` | Lure I–III |
| `treasure_caught` | `CountReachedTrigger` | `treasure_caught` | `Integer` | Luck of the Sea I–III |
| `mob_kills` | `CountReachedTrigger` | `mob_kills` | `Integer` | Sharpness I–V |
| `undead_kills` | `CountReachedTrigger` | `undead_kills` | `Integer` | Smite I–V |
| `arthropod_kills` | `CountReachedTrigger` | `arthropod_kills` | `Integer` | Bane of Arthropods I–V |
| `knockback_fall` | `CountReachedTrigger` | `knockback_fall_distance` | `Integer` | Knockback I–II |
| `fire_kills` | `CountReachedTrigger` | `fire_kills` | `Integer` | Fire Aspect I–II |
| `unique_mobs_killed` | `CountReachedTrigger` | `unique_mobs_killed` | `List<String>` | Looting I–III |
| `sweep_multi_hit` | `CountReachedTrigger` | `sweep_multi_hits` | `Integer` | Sweeping Edge I–III |
| `spear_kills` | `CountReachedTrigger` | `spear_kills` | `Integer` | Lunge I–III |
| `ranged_damage` | `CountReachedTrigger` | `ranged_damage` | `Integer` | Power I–V |
| `long_range_hits` | `CountReachedTrigger` | `long_range_hits` | `Integer` | Punch I–II |
| `nether_bow_kills` | `CountReachedTrigger` | `nether_bow_kills` | `Integer` | Flame |
| `arrows_fired` | `CountReachedTrigger` | `arrows_fired` | `Integer` | Infinity |
| `crossbow_fired` | `CountReachedTrigger` | `crossbow_fired` | `Integer` | Quick Charge I–III |
| `crossbow_kills` | `CountReachedTrigger` | `crossbow_kills` | `Integer` | Piercing I–IV |
| `special_arrow_kills` | `CountReachedTrigger` | `special_arrow_kills` | `Integer` | Multishot |
| `trident_retrievals` | `CountReachedTrigger` | `trident_retrievals` | `Integer` | Loyalty I–III |
| `rain_travel` | `CountReachedTrigger` | `rain_travel` | `Integer` | Riptide I–III |
| `thunderstorm_kills` | `CountReachedTrigger` | `thunderstorm_kills` | `Integer` | Channeling |
| `aquatic_kills` | `CountReachedTrigger` | `aquatic_kills` | `Integer` | Impaling I–V |
| `armored_hits` | `CountReachedTrigger` | `armored_hits` | `Integer` | Breach I–IV |
| `mace_smash_damage` | `CountReachedTrigger` | `mace_smash_damage` | `Integer` | Density I–V |
| `damage_taken` | `CountReachedTrigger` | `damage_taken` | `Integer` | Protection I–IV |
| `fire_damage_taken` | `CountReachedTrigger` | `fire_damage_taken` | `Integer` | Fire Protection I–IV |
| `explosion_hits` | `CountReachedTrigger` | `explosion_hits` | `Integer` | Blast Protection I–IV |
| `projectile_hits_taken` | `CountReachedTrigger` | `projectile_hits_taken` | `Integer` | Projectile Protection I–IV |
| `fall_damage_taken` | `CountReachedTrigger` | `fall_damage_taken` | `Integer` | Feather Falling I–IV |
| `mob_melee_hits` | `CountReachedTrigger` | `mob_melee_hits` | `Integer` | Thorns I–III |
| `underwater_blocks_mined` | `CountReachedTrigger` | `underwater_blocks_mined` | `Integer` | Aqua Affinity |
| `drowning_damage` | `CountReachedTrigger` | `drowning_damage` | `Integer` | Respiration I–III |
| `underwater_travel` | `CountReachedTrigger` | `underwater_travel` | `Integer` | Depth Strider I–III |
| `snowy_travel` | `CountReachedTrigger` | `snowy_travel` | `Integer` | Frost Walker I–II |
| `nether_travel` | `CountReachedTrigger` | `nether_travel` | `Integer` | Soul Speed I–III |
| `sneak_travel` | `CountReachedTrigger` | `sneak_travel` | `Integer` | Swift Sneak I–III |
| `wind_charges_used` | `CountReachedTrigger` | `wind_charges_used` | `Integer` | Wind Burst I–III |
| `enchantments_unlocked` | `CountReachedTrigger` | `enchantments_unlocked` | `Integer` | Reward chain (Unbreaking III/Efficiency V/Fortune III/Mending books) |

### Data Flow — Advancement Tracking
```
Event fires (block break, item break, fishing catch, etc.)
  → Event handler or mixin catches it
  → Increments appropriate counter in player's persistent data attachment
  → Calls trigger.trigger(player, count)
  → Advancement system checks if any sub-criteria thresholds are now met
  → If all sub-criteria met, advancement is granted
  → Shows up in Better Advancements UI with progress (e.g., "3/5")
```

### Data Flow — Enchanting Table Locking
```
Player opens enchanting table
  → EnchantmentMenu constructor → EnchantingTableMixin captures player
  → slotsChanged → getEnchantmentList(registryAccess, item, slot, cost)
    → EnchantingTableMixin sets ThreadLocal<ServerPlayer>
    → @ModifyArg: filters locked enchantments from stream, adds unlocked treasure
    → selectEnchantment(random, item, cost, filteredStream)
      → modifies cost with enchantability bonus + random
      → getAvailableEnchantmentResults(modifiedCost, item, stream)
        → vanilla: iterates each enchantment from maxLevel→minLevel, picks first where cost is in range
        → EnchantmentHelperMixin: reads ThreadLocal, for each result where level > maxUnlocked:
            iterates from maxUnlocked→minLevel, finds highest level valid at this cost
            if none valid → removes (enchantment doesn't appear at this cost)
      → weighted random selection from capped results
    → EnchantingTableMixin clears ThreadLocal
  → Slot shows enchantment clue + level from correctly-filtered list
```

### Tracking Approaches
- **Fabric events:** `PlayerBlockBreakEvents.AFTER` for block mining (BlockBreakHandler), `ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY` for mob kills (MobKillHandler)
- **Mixins:** `LivingEntity.onEquippedItemBroken` for item breaks (ItemBreakMixin), `FishingHook.retrieve` for fishing catches (FishingMixin), `LivingEntity.causeFallDamage` for knockback falls (KnockbackFallMixin), `Player.doSweepAttack` for sweep attacks (SweepAttackMixin), `LivingEntity.hurtServer` for projectile hits (ProjectileHitMixin) and melee hits (MeleeHitMixin), `BowItem.releaseUsing` for bow fires (BowFireMixin), `CrossbowItem.performShooting` for crossbow fires (CrossbowFireMixin), `ThrownTrident.tryPickup` for trident retrievals (TridentPickupMixin), `ServerPlayer.checkMovementStatistics` for rain/underwater/snowy/nether/sneak travel (WetTravelMixin), `WindChargeItem.use` for wind charge uses (WindChargeUseMixin)
- **Mixin on PlayerAdvancements:** `PlayerAdvancements.award` for tracking enchantment advancement completions for reward chain (AdvancementAwardMixin, deferred via `server.execute()` to avoid re-entrancy)
- **Enchanting table:** `EnchantmentMenu.getEnchantmentList` for stream filtering + ThreadLocal management (EnchantingTableMixin), `EnchantmentHelper.getAvailableEnchantmentResults` for level capping with cost-range validation (EnchantmentHelperMixin)

### Build & Datagen Workflow
1. Edit `AdvancementProvider.java` to add/modify advancements
2. Run `./gradlew runDatagen` to generate JSON files into `src/main/generated/`
3. Run `./gradlew build` to compile (generated resources auto-included)
4. Run client from IntelliJ or `./gradlew runClient`

### Adding a New Enchantment Chain (standard pattern)
1. Add a new `AttachmentType<Integer>` in `PlayerDataAttachments.java`
2. Add a new `CountReachedTrigger` instance in `ArcaneProgression.java` and register it
3. Add tracking logic: either extend an existing event handler/mixin, or create a new mixin
4. Add a `generateXxxChain()` call in `AdvancementProvider.java` using `generateCountChain()` helper
5. Run `./gradlew runDatagen && ./gradlew build`

## Conventions

- **Mod ID:** `arcane_progression` (use this as the namespace everywhere)
- **Mappings:** Mojang Mappings only — do NOT use Yarn names
- **Single tab:** ALL advancements in one "Arcane Progression" tab — do NOT create separate tabs per category
- **Category headers:** Auto-granted advancements (InventoryChangeTrigger) that branch from root, grouping chains visually (e.g., `tool_cat`, `melee_cat`, `bow_cat`). Each chain parents to its category header, not to root directly.
- **Advancement IDs:** `arcane_progression:tool/<enchantment_name>` (all under `tool/` path)
- **Criterion trigger IDs:** `arcane_progression:<stat_name>` (e.g., `blocks_mined`, `diamonds_mined`)
- **Data-driven where possible:** Advancements are generated JSON, custom criteria define the bridge between Java events and the advancement system
- **Event-driven tracking:** Hook into Fabric API callbacks or use mixins, not tick-based polling (except for time/biome tracking)
- **Persistent data:** Use Fabric API's `AttachmentType` for per-player counters (stored in playerdata NBT, not vanilla stats)
- **Progress display:** Sub-criteria at step=1 for small counts, step=10 or step=20 for large counts (Power, Infinity), step=64 for Efficiency. `criteriaDetail` is `"Off"` in Better Advancements config. `generateCountChain()` accepts an optional `step` parameter.
- **Reusable triggers:** Use `CountReachedTrigger` for new counters. Only create a new trigger class if the matching logic differs.
- **Keep it simple:** Minimal code, no unnecessary abstractions. Use `generateCountChain()` helper for standard chains.

## Important Files

| File | Purpose |
|------|---------|
| `ArcaneProgression.java` | Mod entry point. Registers all criteria triggers, attachments, event handlers. |
| `criteria/BlocksMinedTrigger.java` | Original trigger for blocks mined (uses `min_blocks` field) |
| `criteria/CountReachedTrigger.java` | Reusable trigger for any counter (uses `min_count` field) |
| `data/PlayerDataAttachments.java` | Defines all `AttachmentType` counters for per-player tracking |
| `events/BlockBreakHandler.java` | Tracks blocks mined, diamonds mined, unique block types on block break |
| `events/MobKillHandler.java` | Tracks all kill-related stats (total, undead, arthropod, fire, spear, nether bow, crossbow, special arrow, thunderstorm, aquatic, unique species) |
| `mixin/AdvancementVisibilityMixin.java` | Forces our advancements visible regardless of depth |
| `mixin/ItemBreakMixin.java` | Tracks tool/weapon/armor breaking through durability depletion |
| `mixin/FishingMixin.java` | Tracks fishing catches and treasure items via FishingHook.retrieve() |
| `mixin/KnockbackFallMixin.java` | Tracks mob fall distance attributed to player via LivingEntity.causeFallDamage() |
| `mixin/SweepAttackMixin.java` | Tracks sweep attacks hitting 3+ mobs via Player.doSweepAttack() |
| `mixin/ProjectileHitMixin.java` | Tracks ranged damage (Power) + long-range hits (Punch) via LivingEntity.hurtServer() |
| `mixin/BowFireMixin.java` | Tracks arrows fired (Infinity) via BowItem.releaseUsing() |
| `mixin/CrossbowFireMixin.java` | Tracks crossbow fires (Quick Charge) via CrossbowItem.performShooting() |
| `mixin/TridentPickupMixin.java` | Tracks trident ground retrievals (Loyalty) via ThrownTrident.tryPickup() |
| `mixin/WetTravelMixin.java` | Tracks distance in rain (Riptide), underwater (Depth Strider), snowy biomes (Frost Walker), Nether (Soul Speed), sneaking (Swift Sneak) via ServerPlayer.checkMovementStatistics() |
| `mixin/MeleeHitMixin.java` | Tracks armored mob hits (Breach) + mace smash damage (Density) via LivingEntity.hurtServer() |
| `mixin/DamageTakenMixin.java` | Tracks damage taken by player (Protection, Fire/Blast/Projectile Prot) via LivingEntity.hurtServer() |
| `mixin/WindChargeUseMixin.java` | Tracks wind charge uses via WindChargeItem.use() |
| `mixin/AdvancementAwardMixin.java` | Tracks enchantment advancement completions for reward chain via PlayerAdvancements.award() |
| `data/EnchantmentUnlockData.java` | Static map of 40 enchantment keys → advancement IDs, `getMaxUnlockedLevel()`, `getUnlockedTreasureHolders()`, ThreadLocal bridge |
| `mixin/EnchantingTableMixin.java` | Mixin on `EnchantmentMenu`: captures player, pre-filters locked enchantments, adds treasure, manages ThreadLocal |
| `mixin/EnchantmentHelperMixin.java` | Mixin on `EnchantmentHelper.getAvailableEnchantmentResults()`: caps levels with cost-range validation |
| `datagen/AdvancementProvider.java` | Generates all advancement JSONs; `generateCountChain()` helper for standard chains |
| `resources/data/arcane_progression/advancement/tool/root.json` | Root advancement (hand-written, auto-trigger) |
| `build.gradle` | Includes generated resources, dev username config, datagen setup |
| `run/config/betteradvancements.json` | Better Advancements config — `criteriaDetail: "Off"` |

## Reference Files (in Claude projects directory)

Located at `C:\Users\Rahul Arora\.claude\projects\E--arcane-progression-template-1-21-11\`:

| File                                   | Contents                                                                                                                          |
| -------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| `enchantment-unlocks-raw.md`           | Complete list of all enchantments and their unlock conditions                                                                     |
| `enchantment-unlocks-mod-reference.md` | Technical reference: vanilla stat analysis, custom trigger list, performance strategy, tracking analysis for every unlock condition |

## Notes

- Vanilla Minecraft tracks `minecraft:mined` per block type but has no "total blocks mined" stat. We track our own counter.
- Better Advancements is purely cosmetic/UI — it reads vanilla advancement data. No API calls or dependencies needed.
- 1.21.11 adds spears (with Lunge enchantment) and nautilus mobs natively. Spear-related advancements will use vanilla spear mechanics.
- This is the last obfuscated MC version. Next version (26.1) switches to unobfuscated + new versioning.
- Performance is not a concern for event-driven triggers like block breaking. Tick-based polling (needed later for underwater time, biome tracking, etc.) should use 20-tick intervals.
- The mod is both server-side and client-side. Advancement granting happens server-side; Better Advancements renders them client-side.
- Advancement background paths in 1.21.x use `"minecraft:gui/advancements/backgrounds/stone"` format (NOT texture paths).
- Vanilla advancement visibility is depth-2 from completed ancestors — our mixin overrides this for our namespace only.
- Dev environment uses `--username Dev` for consistent player identity (configured in build.gradle loom runs).
- Fabric AttachmentType data is stored in `<world>/playerdata/<uuid>.dat` (NBT under `fabric:attachments` tag), separate from vanilla stats JSON.
- MixinExtras 0.5.0 is bundled with Fabric Loader — `@Local` annotation available for capturing local variables in mixins.
- Fishing treasure items (hardcoded in FishingMixin): Bow, Enchanted Book, Fishing Rod, Name Tag, Nautilus Shell, Saddle.
