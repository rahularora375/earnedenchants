# Arcane Progression

A Fabric mod for Minecraft 1.21.11 that adds an enchantment unlock progression system. Enchantments are no longer available by default — players must complete in-game challenges to unlock them tier by tier through an advancement tree.

## How It Works

Every enchantment in the game is locked behind advancements. To unlock Sharpness, kill mobs. To unlock Power, deal ranged damage. To unlock Mending, complete nearly every other enchantment advancement first.

- **41 enchantment chains** spanning 9 categories: Tool, Fishing, Melee, Bow, Crossbow, Trident, Mace, Armor, and Treasure
- **136 advancements** organized in a single "Arcane Progression" tab
- **Per-tier unlocking** — unlock Sharpness I before II before III, etc.
- **Enchanting table integration** — only unlocked enchantments appear at the table, with proper vanilla cost-range validation
- **Treasure enchantments at the table** — Frost Walker, Soul Speed, Swift Sneak, Wind Burst, and Mending become available from the enchanting table once unlocked (normally treasure-only)
- **Mending** is the ultimate goal: complete any 112 of 125 enchantment advancements to unlock it

## Categories

| Category | Enchantments | Unlock Theme |
|----------|-------------|--------------|
| Tool | Efficiency, Fortune, Silk Touch, Unbreaking | Mining blocks, breaking tools |
| Fishing | Lure, Luck of the Sea | Catching fish and treasure |
| Melee | Sharpness, Smite, Bane of Arthropods, Knockback, Fire Aspect, Looting, Sweeping Edge, Lunge | Killing mobs in various ways |
| Bow | Power, Punch, Flame, Infinity | Ranged combat |
| Crossbow | Quick Charge, Multishot, Piercing | Crossbow usage |
| Trident | Loyalty, Riptide, Channeling, Impaling | Trident combat and weather |
| Mace | Breach, Density | Mace smash damage |
| Armor | Protection (all types), Feather Falling, Thorns, Aqua Affinity, Respiration, Depth Strider | Taking damage, underwater activity |
| Treasure | Frost Walker, Soul Speed, Swift Sneak, Wind Burst, Mending | Exploration, travel, mastery |

## Requirements

- **Minecraft** 1.21.11
- **Fabric Loader** 0.18.4+
- **Fabric API**

## Recommended

- [Better Advancements](https://modrinth.com/mod/better-advancements) — Enhanced advancement UI that shows progress bars (e.g., "47/500 mobs killed"). The mod works without it, but the vanilla advancement screen is harder to navigate.
- [Easy Magic](https://modrinth.com/mod/easy-magic) — Shows all enchantment options and allows rerolling. Fully compatible with Arcane Progression's locking system.

## Installation

1. Install Fabric Loader and Fabric API for Minecraft 1.21.11
2. Drop the mod jar into your `mods` folder
3. (Optional) Install Better Advancements and/or Easy Magic

## For Server Admins

The mod works on both client and server. Advancement tracking and enchantment locking happen server-side. Better Advancements is client-side only.

Useful commands:
```
/advancement grant <player> everything                              # Unlock all enchantments
/advancement grant <player> only arcane_progression:tool/sharpness_3  # Unlock Sharpness III specifically
/advancement revoke <player> from arcane_progression:tool/root        # Reset all progress
```

## License

All Rights Reserved
