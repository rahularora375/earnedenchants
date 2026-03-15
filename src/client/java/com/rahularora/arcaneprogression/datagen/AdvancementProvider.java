package com.rahularora.arcaneprogression.datagen;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.criteria.BlocksMinedTrigger;
import com.rahularora.arcaneprogression.criteria.CountReachedTrigger;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends FabricAdvancementProvider {

    private static final int STEP = 64;
    private static final Identifier ROOT = Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "tool/root");

    public AdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        // Category headers (auto-granted)
        Identifier toolId = generateCategoryHeader(consumer, "tool_cat", "Tool", Items.IRON_PICKAXE);
        Identifier fishingId = generateCategoryHeader(consumer, "fishing_cat", "Fishing", Items.FISHING_ROD);
        Identifier meleeId = generateCategoryHeader(consumer, "melee_cat", "Melee", Items.IRON_SWORD);
        Identifier bowId = generateCategoryHeader(consumer, "bow_cat", "Bow", Items.BOW);
        Identifier crossbowId = generateCategoryHeader(consumer, "crossbow_cat", "Crossbow", Items.CROSSBOW);
        Identifier tridentId = generateCategoryHeader(consumer, "trident_cat", "Trident", Items.TRIDENT);
        Identifier maceId = generateCategoryHeader(consumer, "mace_cat", "Mace", Items.MACE);
        Identifier armorId = generateCategoryHeader(consumer, "armor_cat", "Armor", Items.IRON_CHESTPLATE);
        Identifier treasureId = generateCategoryHeader(consumer, "treasure_cat", "Treasure", Items.AMETHYST_SHARD);

        // Tool chains
        generateEfficiencyChain(consumer, toolId);
        generateFortuneChain(consumer, toolId);
        generateSilkTouch(consumer, toolId);
        generateUnbreakingChain(consumer, toolId);

        // Fishing chains
        generateLureChain(consumer, fishingId);
        generateLuckOfTheSeaChain(consumer, fishingId);

        // Melee chains (includes spear — Lunge)
        generateSharpnessChain(consumer, meleeId);
        generateSmiteChain(consumer, meleeId);
        generateBaneOfArthropodsChain(consumer, meleeId);
        generateKnockbackChain(consumer, meleeId);
        generateFireAspectChain(consumer, meleeId);
        generateLootingChain(consumer, meleeId);
        generateSweepingEdgeChain(consumer, meleeId);
        generateLungeChain(consumer, meleeId);

        // Bow chains
        generatePowerChain(consumer, bowId);
        generatePunchChain(consumer, bowId);
        generateFlame(consumer, bowId);
        generateInfinity(consumer, bowId);

        // Crossbow chains
        generateQuickChargeChain(consumer, crossbowId);
        generateMultishot(consumer, crossbowId);
        generatePiercingChain(consumer, crossbowId);

        // Mace chains
        generateBreachChain(consumer, maceId);
        generateDensityChain(consumer, maceId);

        // Armor chains
        generateProtectionChain(consumer, armorId);
        generateFireProtectionChain(consumer, armorId);
        generateBlastProtectionChain(consumer, armorId);
        generateProjectileProtectionChain(consumer, armorId);
        generateFeatherFallingChain(consumer, armorId);
        generateThornsChain(consumer, armorId);
        generateAquaAffinity(consumer, armorId);
        generateRespirationChain(consumer, armorId);
        generateDepthStriderChain(consumer, armorId);

        // Treasure chains
        generateFrostWalkerChain(consumer, treasureId);
        generateSoulSpeedChain(consumer, treasureId);
        generateSwiftSneakChain(consumer, treasureId);
        generateWindBurstChain(consumer, treasureId);
        generateMending(consumer, treasureId);

        // Trident chains
        generateLoyaltyChain(consumer, tridentId);
        generateRiptideChain(consumer, tridentId);
        generateChanneling(consumer, tridentId);
        generateImpalingChain(consumer, tridentId);
    }

    // --- Category header (auto-granted, no toast/chat) ---

    private Identifier generateCategoryHeader(Consumer<AdvancementHolder> consumer,
                                               String name, String title, net.minecraft.world.level.ItemLike icon) {
        AdvancementHolder holder = Advancement.Builder.advancement()
                .parent(ROOT)
                .display(new DisplayInfo(
                        new ItemStack(icon),
                        Component.literal(title),
                        Component.literal(""),
                        Optional.empty(), AdvancementType.TASK, false, false, false
                ))
                .addCriterion("auto", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                        new InventoryChangeTrigger.TriggerInstance(
                                Optional.empty(),
                                InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                                List.of()
                        )
                ))
                .save(consumer, ArcaneProgression.MOD_ID + ":tool/" + name);
        return holder.id();
    }

    // --- Shared record & helper for CountReachedTrigger chains ---

    private record Level(String name, String title, String desc, AdvancementType frame, int target) {}

    private void generateCountChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent,
                                     List<Level> levels, ItemStack icon, String criterionPrefix,
                                     CountReachedTrigger trigger) {
        generateCountChain(consumer, categoryParent, levels, icon, criterionPrefix, trigger, 1);
    }

    private void generateCountChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent,
                                     List<Level> levels, ItemStack icon, String criterionPrefix,
                                     CountReachedTrigger trigger, int step) {
        Identifier parentId = categoryParent;
        for (Level level : levels) {
            Advancement.Builder builder = Advancement.Builder.advancement()
                    .parent(parentId)
                    .display(new DisplayInfo(
                            icon, Component.literal(level.title()), Component.literal(level.desc()),
                            Optional.empty(), level.frame(), true, true, false
                    ));

            List<String> criteriaNames = new ArrayList<>();
            for (int i = step; i <= level.target(); i += step) {
                String name = criterionPrefix + i;
                builder.addCriterion(name, trigger.createCriterion(
                        new CountReachedTrigger.TriggerInstance(Optional.empty(), i)));
                criteriaNames.add(name);
            }
            if (level.target() % step != 0) {
                String name = criterionPrefix + level.target();
                builder.addCriterion(name, trigger.createCriterion(
                        new CountReachedTrigger.TriggerInstance(Optional.empty(), level.target())));
                criteriaNames.add(name);
            }

            builder.requirements(AdvancementRequirements.allOf(criteriaNames));
            AdvancementHolder holder = builder.save(consumer, ArcaneProgression.MOD_ID + ":tool/" + level.name());
            parentId = holder.id();
        }
    }

    // --- Efficiency (unique: step=64, per-level icons, BlocksMinedTrigger) ---

    private void generateEfficiencyChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        record EffLevel(String name, String title, String desc, ItemStack icon, AdvancementType frame, int target) {}

        List<EffLevel> levels = List.of(
                new EffLevel("efficiency_1", "Efficiency I", "Mine 320 blocks", new ItemStack(Items.WOODEN_PICKAXE), AdvancementType.TASK, 320),
                new EffLevel("efficiency_2", "Efficiency II", "Mine 960 blocks", new ItemStack(Items.STONE_PICKAXE), AdvancementType.TASK, 960),
                new EffLevel("efficiency_3", "Efficiency III", "Mine 1,920 blocks", new ItemStack(Items.IRON_PICKAXE), AdvancementType.TASK, 1920),
                new EffLevel("efficiency_4", "Efficiency IV", "Mine 3,840 blocks", new ItemStack(Items.DIAMOND_PICKAXE), AdvancementType.TASK, 3840),
                new EffLevel("efficiency_5", "Efficiency V", "Mine 6,400 blocks", new ItemStack(Items.NETHERITE_PICKAXE), AdvancementType.CHALLENGE, 6400)
        );

        Identifier parentId = categoryParent;
        for (EffLevel level : levels) {
            Advancement.Builder builder = Advancement.Builder.advancement()
                    .parent(parentId)
                    .display(new DisplayInfo(
                            level.icon(), Component.literal(level.title()), Component.literal(level.desc()),
                            Optional.empty(), level.frame(), true, true, false
                    ));

            List<String> criteriaNames = new ArrayList<>();
            for (int threshold = STEP; threshold <= level.target(); threshold += STEP) {
                String name = "mine_" + threshold;
                builder.addCriterion(name, ArcaneProgression.BLOCKS_MINED_TRIGGER.createCriterion(
                        new BlocksMinedTrigger.TriggerInstance(Optional.empty(), threshold)));
                criteriaNames.add(name);
            }

            builder.requirements(AdvancementRequirements.allOf(criteriaNames));
            AdvancementHolder holder = builder.save(consumer, ArcaneProgression.MOD_ID + ":tool/" + level.name());
            parentId = holder.id();
        }
    }

    // --- Silk Touch (standalone, not a chain) ---

    private void generateSilkTouch(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .parent(categoryParent)
                .display(new DisplayInfo(
                        new ItemStack(Items.GRASS_BLOCK),
                        Component.literal("Silk Touch"),
                        Component.literal("Mine 40 unique block types"),
                        Optional.empty(), AdvancementType.GOAL, true, true, false
                ));

        List<String> criteriaNames = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            String name = "unique_" + i;
            builder.addCriterion(name, ArcaneProgression.UNIQUE_BLOCKS_MINED_TRIGGER.createCriterion(
                    new CountReachedTrigger.TriggerInstance(Optional.empty(), i)));
            criteriaNames.add(name);
        }

        builder.requirements(AdvancementRequirements.allOf(criteriaNames));
        builder.save(consumer, ArcaneProgression.MOD_ID + ":tool/silk_touch");
    }

    // --- Standard chains using shared helper ---

    private void generateFortuneChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("fortune_1", "Fortune I", "Mine 16 diamonds", AdvancementType.TASK, 16),
                new Level("fortune_2", "Fortune II", "Mine 32 diamonds", AdvancementType.TASK, 32),
                new Level("fortune_3", "Fortune III", "Mine 64 diamonds", AdvancementType.CHALLENGE, 64)
        ), new ItemStack(Items.DIAMOND), "diamond_", ArcaneProgression.DIAMONDS_MINED_TRIGGER);
    }

    private void generateUnbreakingChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("unbreaking_1", "Unbreaking I", "Break 1 item through use", AdvancementType.TASK, 1),
                new Level("unbreaking_2", "Unbreaking II", "Break 3 items through use", AdvancementType.TASK, 3),
                new Level("unbreaking_3", "Unbreaking III", "Break 6 items through use", AdvancementType.CHALLENGE, 6)
        ), new ItemStack(Items.ANVIL), "break_", ArcaneProgression.TOOLS_BROKEN_TRIGGER);
    }

    private void generateLureChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("lure_1", "Lure I", "Catch 25 fish", AdvancementType.TASK, 25),
                new Level("lure_2", "Lure II", "Catch 75 fish", AdvancementType.TASK, 75),
                new Level("lure_3", "Lure III", "Catch 150 fish", AdvancementType.CHALLENGE, 150)
        ), new ItemStack(Items.FISHING_ROD), "catch_", ArcaneProgression.FISH_CAUGHT_TRIGGER);
    }

    private void generateLuckOfTheSeaChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("luck_of_the_sea_1", "Luck of the Sea I", "Catch 3 treasure items", AdvancementType.TASK, 3),
                new Level("luck_of_the_sea_2", "Luck of the Sea II", "Catch 8 treasure items", AdvancementType.TASK, 8),
                new Level("luck_of_the_sea_3", "Luck of the Sea III", "Catch 15 treasure items", AdvancementType.CHALLENGE, 15)
        ), new ItemStack(Items.NAUTILUS_SHELL), "treasure_", ArcaneProgression.TREASURE_CAUGHT_TRIGGER);
    }

    private void generateSharpnessChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("sharpness_1", "Sharpness I", "Kill 25 mobs", AdvancementType.TASK, 25),
                new Level("sharpness_2", "Sharpness II", "Kill 75 mobs", AdvancementType.TASK, 75),
                new Level("sharpness_3", "Sharpness III", "Kill 150 mobs", AdvancementType.TASK, 150),
                new Level("sharpness_4", "Sharpness IV", "Kill 300 mobs", AdvancementType.TASK, 300),
                new Level("sharpness_5", "Sharpness V", "Kill 500 mobs", AdvancementType.CHALLENGE, 500)
        ), new ItemStack(Items.IRON_SWORD), "kill_", ArcaneProgression.MOB_KILLS_TRIGGER);
    }

    private void generateSmiteChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("smite_1", "Smite I", "Kill 10 undead mobs", AdvancementType.TASK, 10),
                new Level("smite_2", "Smite II", "Kill 30 undead mobs", AdvancementType.TASK, 30),
                new Level("smite_3", "Smite III", "Kill 60 undead mobs", AdvancementType.TASK, 60),
                new Level("smite_4", "Smite IV", "Kill 120 undead mobs", AdvancementType.TASK, 120),
                new Level("smite_5", "Smite V", "Kill 200 undead mobs", AdvancementType.CHALLENGE, 200)
        ), new ItemStack(Items.ROTTEN_FLESH), "undead_", ArcaneProgression.UNDEAD_KILLS_TRIGGER);
    }

    private void generateBaneOfArthropodsChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("bane_of_arthropods_1", "Bane of Arthropods I", "Kill 10 arthropod mobs", AdvancementType.TASK, 10),
                new Level("bane_of_arthropods_2", "Bane of Arthropods II", "Kill 30 arthropod mobs", AdvancementType.TASK, 30),
                new Level("bane_of_arthropods_3", "Bane of Arthropods III", "Kill 60 arthropod mobs", AdvancementType.TASK, 60),
                new Level("bane_of_arthropods_4", "Bane of Arthropods IV", "Kill 120 arthropod mobs", AdvancementType.TASK, 120),
                new Level("bane_of_arthropods_5", "Bane of Arthropods V", "Kill 200 arthropod mobs", AdvancementType.CHALLENGE, 200)
        ), new ItemStack(Items.SPIDER_EYE), "arthropod_", ArcaneProgression.ARTHROPOD_KILLS_TRIGGER);
    }

    private void generateKnockbackChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("knockback_1", "Knockback I", "Cause 50 blocks of mob fall distance", AdvancementType.TASK, 50),
                new Level("knockback_2", "Knockback II", "Cause 150 blocks of mob fall distance", AdvancementType.CHALLENGE, 150)
        ), new ItemStack(Items.PISTON), "fall_", ArcaneProgression.KNOCKBACK_FALL_TRIGGER);
    }

    private void generateFireAspectChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("fire_aspect_1", "Fire Aspect I", "Kill 20 burning mobs", AdvancementType.TASK, 20),
                new Level("fire_aspect_2", "Fire Aspect II", "Kill 60 burning mobs", AdvancementType.CHALLENGE, 60)
        ), new ItemStack(Items.BLAZE_POWDER), "fire_kill_", ArcaneProgression.FIRE_KILLS_TRIGGER);
    }

    private void generateLootingChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("looting_1", "Looting I", "Kill 10 unique mob species", AdvancementType.TASK, 10),
                new Level("looting_2", "Looting II", "Kill 20 unique mob species", AdvancementType.TASK, 20),
                new Level("looting_3", "Looting III", "Kill 30 unique mob species", AdvancementType.CHALLENGE, 30)
        ), new ItemStack(Items.GOLDEN_SWORD), "species_", ArcaneProgression.UNIQUE_MOBS_KILLED_TRIGGER);
    }

    private void generateSweepingEdgeChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("sweeping_edge_1", "Sweeping Edge I", "Hit 3+ mobs in a single sweep 25 times", AdvancementType.TASK, 25),
                new Level("sweeping_edge_2", "Sweeping Edge II", "Hit 3+ mobs in a single sweep 75 times", AdvancementType.TASK, 75),
                new Level("sweeping_edge_3", "Sweeping Edge III", "Hit 3+ mobs in a single sweep 200 times", AdvancementType.CHALLENGE, 200)
        ), new ItemStack(Items.DIAMOND_SWORD), "sweep_", ArcaneProgression.SWEEP_MULTI_HIT_TRIGGER);
    }

    private void generateLungeChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("lunge_1", "Lunge I", "Kill 15 mobs with a spear", AdvancementType.TASK, 15),
                new Level("lunge_2", "Lunge II", "Kill 50 mobs with a spear", AdvancementType.TASK, 50),
                new Level("lunge_3", "Lunge III", "Kill 120 mobs with a spear", AdvancementType.CHALLENGE, 120)
        ), new ItemStack(Items.IRON_SPEAR), "spear_kill_", ArcaneProgression.SPEAR_KILLS_TRIGGER);
    }

    // --- Bow enchantments ---

    private void generatePowerChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("power_1", "Power I", "Deal 100 ranged damage", AdvancementType.TASK, 100),
                new Level("power_2", "Power II", "Deal 300 ranged damage", AdvancementType.TASK, 300),
                new Level("power_3", "Power III", "Deal 600 ranged damage", AdvancementType.TASK, 600),
                new Level("power_4", "Power IV", "Deal 1,200 ranged damage", AdvancementType.TASK, 1200),
                new Level("power_5", "Power V", "Deal 2,000 ranged damage", AdvancementType.CHALLENGE, 2000)
        ), new ItemStack(Items.BOW), "ranged_dmg_", ArcaneProgression.RANGED_DAMAGE_TRIGGER, 20);
    }

    private void generatePunchChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("punch_1", "Punch I", "Hit mobs from 24+ blocks away 15 times", AdvancementType.TASK, 15),
                new Level("punch_2", "Punch II", "Hit mobs from 24+ blocks away 50 times", AdvancementType.CHALLENGE, 50)
        ), new ItemStack(Items.ARROW), "long_range_", ArcaneProgression.LONG_RANGE_HITS_TRIGGER);
    }

    private void generateFlame(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("flame", "Flame", "Kill 25 mobs with a bow in the Nether", AdvancementType.GOAL, 25)
        ), new ItemStack(Items.FIRE_CHARGE), "nether_bow_kill_", ArcaneProgression.NETHER_BOW_KILLS_TRIGGER);
    }

    private void generateInfinity(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("infinity", "Infinity", "Fire 1,000 arrows", AdvancementType.CHALLENGE, 1000)
        ), new ItemStack(Items.SPECTRAL_ARROW), "arrow_", ArcaneProgression.ARROWS_FIRED_TRIGGER, 20);
    }

    // --- Crossbow enchantments ---

    private void generateQuickChargeChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("quick_charge_1", "Quick Charge I", "Fire crossbow 100 times", AdvancementType.TASK, 100),
                new Level("quick_charge_2", "Quick Charge II", "Fire crossbow 300 times", AdvancementType.TASK, 300),
                new Level("quick_charge_3", "Quick Charge III", "Fire crossbow 600 times", AdvancementType.CHALLENGE, 600)
        ), new ItemStack(Items.CROSSBOW), "xbow_fire_", ArcaneProgression.CROSSBOW_FIRED_TRIGGER, 10);
    }

    private void generateMultishot(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("multishot", "Multishot", "Kill 15 mobs with special arrows", AdvancementType.GOAL, 15)
        ), new ItemStack(Items.CROSSBOW), "special_kill_", ArcaneProgression.SPECIAL_ARROW_KILLS_TRIGGER);
    }

    private void generatePiercingChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("piercing_1", "Piercing I", "Kill 25 mobs with a crossbow", AdvancementType.TASK, 25),
                new Level("piercing_2", "Piercing II", "Kill 75 mobs with a crossbow", AdvancementType.TASK, 75),
                new Level("piercing_3", "Piercing III", "Kill 150 mobs with a crossbow", AdvancementType.TASK, 150),
                new Level("piercing_4", "Piercing IV", "Kill 300 mobs with a crossbow", AdvancementType.CHALLENGE, 300)
        ), new ItemStack(Items.ARROW), "xbow_kill_", ArcaneProgression.CROSSBOW_KILLS_TRIGGER);
    }

    // --- Armor enchantments ---

    private void generateProtectionChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("protection_1", "Protection I", "Take 200 total damage", AdvancementType.TASK, 200),
                new Level("protection_2", "Protection II", "Take 600 total damage", AdvancementType.TASK, 600),
                new Level("protection_3", "Protection III", "Take 1,500 total damage", AdvancementType.TASK, 1500),
                new Level("protection_4", "Protection IV", "Take 3,000 total damage", AdvancementType.CHALLENGE, 3000)
        ), new ItemStack(Items.IRON_CHESTPLATE), "dmg_taken_", ArcaneProgression.DAMAGE_TAKEN_TRIGGER, 10);
    }

    private void generateFireProtectionChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("fire_protection_1", "Fire Protection I", "Take 60 fire damage", AdvancementType.TASK, 60),
                new Level("fire_protection_2", "Fire Protection II", "Take 150 fire damage", AdvancementType.TASK, 150),
                new Level("fire_protection_3", "Fire Protection III", "Take 400 fire damage", AdvancementType.TASK, 400),
                new Level("fire_protection_4", "Fire Protection IV", "Take 800 fire damage", AdvancementType.CHALLENGE, 800)
        ), new ItemStack(Items.MAGMA_BLOCK), "fire_dmg_", ArcaneProgression.FIRE_DAMAGE_TAKEN_TRIGGER, 10);
    }

    private void generateBlastProtectionChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("blast_protection_1", "Blast Protection I", "Take explosion damage 10 times", AdvancementType.TASK, 10),
                new Level("blast_protection_2", "Blast Protection II", "Take explosion damage 25 times", AdvancementType.TASK, 25),
                new Level("blast_protection_3", "Blast Protection III", "Take explosion damage 60 times", AdvancementType.TASK, 60),
                new Level("blast_protection_4", "Blast Protection IV", "Take explosion damage 120 times", AdvancementType.CHALLENGE, 120)
        ), new ItemStack(Items.TNT), "explosion_hit_", ArcaneProgression.EXPLOSION_HITS_TRIGGER);
    }

    private void generateProjectileProtectionChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("projectile_protection_1", "Projectile Protection I", "Get hit by projectiles 30 times", AdvancementType.TASK, 30),
                new Level("projectile_protection_2", "Projectile Protection II", "Get hit by projectiles 80 times", AdvancementType.TASK, 80),
                new Level("projectile_protection_3", "Projectile Protection III", "Get hit by projectiles 200 times", AdvancementType.TASK, 200),
                new Level("projectile_protection_4", "Projectile Protection IV", "Get hit by projectiles 500 times", AdvancementType.CHALLENGE, 500)
        ), new ItemStack(Items.SHIELD), "proj_hit_", ArcaneProgression.PROJECTILE_HITS_TAKEN_TRIGGER);
    }

    private void generateFeatherFallingChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("feather_falling_1", "Feather Falling I", "Take 75 fall damage", AdvancementType.TASK, 75),
                new Level("feather_falling_2", "Feather Falling II", "Take 150 fall damage", AdvancementType.TASK, 150),
                new Level("feather_falling_3", "Feather Falling III", "Take 300 fall damage", AdvancementType.TASK, 300),
                new Level("feather_falling_4", "Feather Falling IV", "Take 500 fall damage", AdvancementType.CHALLENGE, 500)
        ), new ItemStack(Items.FEATHER), "fall_dmg_", ArcaneProgression.FALL_DAMAGE_TAKEN_TRIGGER, 10);
    }

    private void generateThornsChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("thorns_1", "Thorns I", "Get hit by mobs in melee 100 times", AdvancementType.TASK, 100),
                new Level("thorns_2", "Thorns II", "Get hit by mobs in melee 300 times", AdvancementType.TASK, 300),
                new Level("thorns_3", "Thorns III", "Get hit by mobs in melee 700 times", AdvancementType.CHALLENGE, 700)
        ), new ItemStack(Items.CACTUS), "mob_hit_", ArcaneProgression.MOB_MELEE_HITS_TRIGGER, 10);
    }

    private void generateAquaAffinity(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("aqua_affinity", "Aqua Affinity", "Mine 200 blocks underwater", AdvancementType.GOAL, 200)
        ), new ItemStack(Items.CONDUIT), "uw_mine_", ArcaneProgression.UNDERWATER_BLOCKS_MINED_TRIGGER, 10);
    }

    private void generateRespirationChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("respiration_1", "Respiration I", "Take 20 drowning damage", AdvancementType.TASK, 20),
                new Level("respiration_2", "Respiration II", "Take 60 drowning damage", AdvancementType.TASK, 60),
                new Level("respiration_3", "Respiration III", "Take 150 drowning damage", AdvancementType.CHALLENGE, 150)
        ), new ItemStack(Items.PUFFERFISH), "drown_dmg_", ArcaneProgression.DROWNING_DAMAGE_TRIGGER);
    }

    private void generateDepthStriderChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("depth_strider_1", "Depth Strider I", "Travel 500 blocks underwater", AdvancementType.TASK, 500),
                new Level("depth_strider_2", "Depth Strider II", "Travel 1,500 blocks underwater", AdvancementType.TASK, 1500),
                new Level("depth_strider_3", "Depth Strider III", "Travel 5,000 blocks underwater", AdvancementType.CHALLENGE, 5000)
        ), new ItemStack(Items.HEART_OF_THE_SEA), "uw_travel_", ArcaneProgression.UNDERWATER_TRAVEL_TRIGGER, 10);
    }

    // --- Treasure enchantments ---

    private void generateFrostWalkerChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("frost_walker_1", "Frost Walker I", "Travel 1,000 blocks in snowy biomes", AdvancementType.TASK, 1000),
                new Level("frost_walker_2", "Frost Walker II", "Travel 3,000 blocks in snowy biomes", AdvancementType.CHALLENGE, 3000)
        ), new ItemStack(Items.POWDER_SNOW_BUCKET), "snow_travel_", ArcaneProgression.SNOWY_TRAVEL_TRIGGER, 10);
    }

    private void generateSoulSpeedChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("soul_speed_1", "Soul Speed I", "Travel 1,000 blocks in the Nether", AdvancementType.TASK, 1000),
                new Level("soul_speed_2", "Soul Speed II", "Travel 4,000 blocks in the Nether", AdvancementType.TASK, 4000),
                new Level("soul_speed_3", "Soul Speed III", "Travel 10,000 blocks in the Nether", AdvancementType.CHALLENGE, 10000)
        ), new ItemStack(Items.SOUL_SAND), "nether_travel_", ArcaneProgression.NETHER_TRAVEL_TRIGGER, 10);
    }

    private void generateSwiftSneakChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("swift_sneak_1", "Swift Sneak I", "Travel 1,000 blocks while sneaking", AdvancementType.TASK, 1000),
                new Level("swift_sneak_2", "Swift Sneak II", "Travel 3,000 blocks while sneaking", AdvancementType.TASK, 3000),
                new Level("swift_sneak_3", "Swift Sneak III", "Travel 6,000 blocks while sneaking", AdvancementType.CHALLENGE, 6000)
        ), new ItemStack(Items.SCULK_SHRIEKER), "sneak_travel_", ArcaneProgression.SNEAK_TRAVEL_TRIGGER, 10);
    }

    private void generateWindBurstChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("wind_burst_1", "Wind Burst I", "Use 15 wind charges", AdvancementType.TASK, 15),
                new Level("wind_burst_2", "Wind Burst II", "Use 40 wind charges", AdvancementType.TASK, 40),
                new Level("wind_burst_3", "Wind Burst III", "Use 80 wind charges", AdvancementType.CHALLENGE, 80)
        ), new ItemStack(Items.WIND_CHARGE), "wind_use_", ArcaneProgression.WIND_CHARGES_USED_TRIGGER);
    }

    private void generateMending(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("mending", "Mending", "Complete any 112 enchantment advancements", AdvancementType.CHALLENGE, 112)
        ), new ItemStack(Items.ENCHANTED_BOOK), "unlock_", ArcaneProgression.ENCHANTMENTS_UNLOCKED_TRIGGER);
    }

    // --- Mace enchantments ---

    private void generateBreachChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("breach_1", "Breach I", "Hit armored mobs with a weapon 5 times", AdvancementType.TASK, 5),
                new Level("breach_2", "Breach II", "Hit armored mobs with a weapon 15 times", AdvancementType.TASK, 15),
                new Level("breach_3", "Breach III", "Hit armored mobs with a weapon 40 times", AdvancementType.TASK, 40),
                new Level("breach_4", "Breach IV", "Hit armored mobs with a weapon 100 times", AdvancementType.CHALLENGE, 100)
        ), new ItemStack(Items.IRON_CHESTPLATE), "armored_hit_", ArcaneProgression.ARMORED_HITS_TRIGGER);
    }

    private void generateDensityChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("density_1", "Density I", "Deal 50 mace smash damage", AdvancementType.TASK, 50),
                new Level("density_2", "Density II", "Deal 150 mace smash damage", AdvancementType.TASK, 150),
                new Level("density_3", "Density III", "Deal 350 mace smash damage", AdvancementType.TASK, 350),
                new Level("density_4", "Density IV", "Deal 750 mace smash damage", AdvancementType.TASK, 750),
                new Level("density_5", "Density V", "Deal 1,500 mace smash damage", AdvancementType.CHALLENGE, 1500)
        ), new ItemStack(Items.MACE), "smash_dmg_", ArcaneProgression.MACE_SMASH_DAMAGE_TRIGGER, 10);
    }

    // --- Trident enchantments ---

    private void generateLoyaltyChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("loyalty_1", "Loyalty I", "Retrieve trident from the ground 15 times", AdvancementType.TASK, 15),
                new Level("loyalty_2", "Loyalty II", "Retrieve trident from the ground 40 times", AdvancementType.TASK, 40),
                new Level("loyalty_3", "Loyalty III", "Retrieve trident from the ground 100 times", AdvancementType.CHALLENGE, 100)
        ), new ItemStack(Items.WOLF_ARMOR), "retrieve_", ArcaneProgression.TRIDENT_RETRIEVALS_TRIGGER);
    }

    private void generateRiptideChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("riptide_1", "Riptide I", "Travel 500 blocks in rain", AdvancementType.TASK, 500),
                new Level("riptide_2", "Riptide II", "Travel 2,000 blocks in rain", AdvancementType.TASK, 2000),
                new Level("riptide_3", "Riptide III", "Travel 5,000 blocks in rain", AdvancementType.CHALLENGE, 5000)
        ), new ItemStack(Items.WATER_BUCKET), "wet_travel_", ArcaneProgression.RAIN_TRAVEL_TRIGGER, 10);
    }

    private void generateChanneling(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("channeling", "Channeling", "Kill 10 mobs during a thunderstorm", AdvancementType.GOAL, 10)
        ), new ItemStack(Items.LIGHTNING_ROD), "thunder_kill_", ArcaneProgression.THUNDERSTORM_KILLS_TRIGGER);
    }

    private void generateImpalingChain(Consumer<AdvancementHolder> consumer, Identifier categoryParent) {
        generateCountChain(consumer, categoryParent, List.of(
                new Level("impaling_1", "Impaling I", "Kill 10 aquatic mobs", AdvancementType.TASK, 10),
                new Level("impaling_2", "Impaling II", "Kill 25 aquatic mobs", AdvancementType.TASK, 25),
                new Level("impaling_3", "Impaling III", "Kill 60 aquatic mobs", AdvancementType.TASK, 60),
                new Level("impaling_4", "Impaling IV", "Kill 120 aquatic mobs", AdvancementType.TASK, 120),
                new Level("impaling_5", "Impaling V", "Kill 200 aquatic mobs", AdvancementType.CHALLENGE, 200)
        ), new ItemStack(Items.SALMON), "aquatic_kill_", ArcaneProgression.AQUATIC_KILLS_TRIGGER);
    }
}