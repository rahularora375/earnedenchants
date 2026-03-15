package com.rahularora.arcaneprogression.data;

import com.mojang.serialization.Codec;
import com.rahularora.arcaneprogression.ArcaneProgression;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PlayerDataAttachments {
    public static final AttachmentType<Integer> BLOCKS_MINED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "blocks_mined"));

    public static final AttachmentType<Integer> DIAMONDS_MINED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "diamonds_mined"));

    public static final AttachmentType<List<String>> UNIQUE_BLOCKS_MINED = AttachmentRegistry.<List<String>>builder()
            .persistent(Codec.STRING.listOf())
            .copyOnDeath()
            .initializer(ArrayList::new)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "unique_blocks_mined"));

    public static final AttachmentType<Integer> TOOLS_BROKEN = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "tools_broken"));

    public static final AttachmentType<Integer> FISH_CAUGHT = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "fish_caught"));

    public static final AttachmentType<Integer> TREASURE_CAUGHT = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "treasure_caught"));

    public static final AttachmentType<Integer> MOB_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "mob_kills"));

    public static final AttachmentType<Integer> UNDEAD_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "undead_kills"));

    public static final AttachmentType<Integer> ARTHROPOD_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "arthropod_kills"));

    public static final AttachmentType<Integer> FIRE_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "fire_kills"));

    public static final AttachmentType<Integer> KNOCKBACK_FALL_DISTANCE = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "knockback_fall_distance"));

    public static final AttachmentType<List<String>> UNIQUE_MOBS_KILLED = AttachmentRegistry.<List<String>>builder()
            .persistent(Codec.STRING.listOf())
            .copyOnDeath()
            .initializer(ArrayList::new)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "unique_mobs_killed"));

    public static final AttachmentType<Integer> SWEEP_MULTI_HITS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "sweep_multi_hits"));

    public static final AttachmentType<Integer> SPEAR_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "spear_kills"));

    // Bow enchantments
    public static final AttachmentType<Integer> RANGED_DAMAGE = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "ranged_damage"));

    public static final AttachmentType<Integer> LONG_RANGE_HITS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "long_range_hits"));

    public static final AttachmentType<Integer> NETHER_BOW_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "nether_bow_kills"));

    public static final AttachmentType<Integer> ARROWS_FIRED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "arrows_fired"));

    // Crossbow enchantments
    public static final AttachmentType<Integer> CROSSBOW_FIRED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "crossbow_fired"));

    public static final AttachmentType<Integer> CROSSBOW_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "crossbow_kills"));

    public static final AttachmentType<Integer> SPECIAL_ARROW_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "special_arrow_kills"));

    // Trident enchantments
    public static final AttachmentType<Integer> TRIDENT_RETRIEVALS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "trident_retrievals"));

    public static final AttachmentType<Integer> RAIN_TRAVEL = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "rain_travel"));

    public static final AttachmentType<Integer> THUNDERSTORM_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "thunderstorm_kills"));

    public static final AttachmentType<Integer> AQUATIC_KILLS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "aquatic_kills"));

    // Mace enchantments
    public static final AttachmentType<Integer> ARMORED_HITS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "armored_hits"));

    public static final AttachmentType<Integer> MACE_SMASH_DAMAGE = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "mace_smash_damage"));

    // Armor enchantments
    public static final AttachmentType<Integer> DAMAGE_TAKEN = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "damage_taken"));

    public static final AttachmentType<Integer> FIRE_DAMAGE_TAKEN = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "fire_damage_taken"));

    public static final AttachmentType<Integer> EXPLOSION_HITS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "explosion_hits"));

    public static final AttachmentType<Integer> PROJECTILE_HITS_TAKEN = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "projectile_hits_taken"));

    public static final AttachmentType<Integer> FALL_DAMAGE_TAKEN = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "fall_damage_taken"));

    public static final AttachmentType<Integer> MOB_MELEE_HITS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "mob_melee_hits"));

    public static final AttachmentType<Integer> UNDERWATER_BLOCKS_MINED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "underwater_blocks_mined"));

    public static final AttachmentType<Integer> DROWNING_DAMAGE = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "drowning_damage"));

    public static final AttachmentType<Integer> UNDERWATER_TRAVEL = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "underwater_travel"));

    // Treasure enchantments
    public static final AttachmentType<Integer> SNOWY_TRAVEL = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "snowy_travel"));

    public static final AttachmentType<Integer> NETHER_TRAVEL = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "nether_travel"));

    public static final AttachmentType<Integer> SNEAK_TRAVEL = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "sneak_travel"));

    public static final AttachmentType<Integer> WIND_CHARGES_USED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "wind_charges_used"));

    public static final AttachmentType<Integer> ENCHANTMENTS_UNLOCKED = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT)
            .copyOnDeath()
            .initializer(() -> 0)
            .buildAndRegister(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "enchantments_unlocked"));

    public static void register() {
        // Forces static initialization
    }
}
