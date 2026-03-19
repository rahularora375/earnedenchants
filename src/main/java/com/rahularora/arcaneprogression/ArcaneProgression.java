package com.rahularora.arcaneprogression;

import com.rahularora.arcaneprogression.criteria.BlocksMinedTrigger;
import com.rahularora.arcaneprogression.criteria.CountReachedTrigger;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import com.rahularora.arcaneprogression.events.BlockBreakHandler;
import com.rahularora.arcaneprogression.events.MobKillHandler;
import com.rahularora.arcaneprogression.network.VersionPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneProgression implements ModInitializer {
	public static final String MOD_ID = "earnedenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String MOD_VERSION = FabricLoader.getInstance()
			.getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();

	public static final BlocksMinedTrigger BLOCKS_MINED_TRIGGER = new BlocksMinedTrigger();
	public static final CountReachedTrigger DIAMONDS_MINED_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger UNIQUE_BLOCKS_MINED_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger TOOLS_BROKEN_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger FISH_CAUGHT_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger TREASURE_CAUGHT_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger HOSTILE_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger UNDEAD_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger ARTHROPOD_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger FIRE_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger KNOCKBACK_FALL_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger UNIQUE_MOBS_KILLED_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger SWEEP_MULTI_HIT_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger SPEAR_KILLS_TRIGGER = new CountReachedTrigger();

	// Bow triggers
	public static final CountReachedTrigger RANGED_DAMAGE_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger LONG_RANGE_HITS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger NETHER_BOW_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger ARROWS_FIRED_TRIGGER = new CountReachedTrigger();

	// Crossbow triggers
	public static final CountReachedTrigger CROSSBOW_FIRED_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger CROSSBOW_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger SPECIAL_ARROW_KILLS_TRIGGER = new CountReachedTrigger();

	// Mace triggers
	public static final CountReachedTrigger ARMORED_HITS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger MACE_SMASH_DAMAGE_TRIGGER = new CountReachedTrigger();

	// Armor triggers
	public static final CountReachedTrigger DAMAGE_TAKEN_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger FIRE_DAMAGE_TAKEN_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger EXPLOSION_HITS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger PROJECTILE_HITS_TAKEN_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger FALL_DAMAGE_TAKEN_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger MOB_MELEE_HITS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger UNDERWATER_BLOCKS_MINED_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger DROWNING_DAMAGE_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger UNDERWATER_TRAVEL_TRIGGER = new CountReachedTrigger();

	// Treasure triggers
	public static final CountReachedTrigger SNOWY_TRAVEL_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger NETHER_TRAVEL_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger SNEAK_TRAVEL_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger WIND_CHARGES_USED_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger ENCHANTMENTS_UNLOCKED_TRIGGER = new CountReachedTrigger();

	// Trident triggers
	public static final CountReachedTrigger TRIDENT_RETRIEVALS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger RAIN_TRAVEL_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger THUNDERSTORM_KILLS_TRIGGER = new CountReachedTrigger();
	public static final CountReachedTrigger AQUATIC_KILLS_TRIGGER = new CountReachedTrigger();

	@Override
	public void onInitialize() {
		// Register data attachments
		PlayerDataAttachments.register();

		// Register custom criteria triggers
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "blocks_mined"),
				BLOCKS_MINED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "diamonds_mined"),
				DIAMONDS_MINED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "unique_blocks_mined"),
				UNIQUE_BLOCKS_MINED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "tools_broken"),
				TOOLS_BROKEN_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "fish_caught"),
				FISH_CAUGHT_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "treasure_caught"),
				TREASURE_CAUGHT_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "hostile_kills"),
				HOSTILE_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "undead_kills"),
				UNDEAD_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "arthropod_kills"),
				ARTHROPOD_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "fire_kills"),
				FIRE_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "knockback_fall"),
				KNOCKBACK_FALL_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "unique_mobs_killed"),
				UNIQUE_MOBS_KILLED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "sweep_multi_hit"),
				SWEEP_MULTI_HIT_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "spear_kills"),
				SPEAR_KILLS_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "ranged_damage"),
				RANGED_DAMAGE_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "long_range_hits"),
				LONG_RANGE_HITS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "nether_bow_kills"),
				NETHER_BOW_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "arrows_fired"),
				ARROWS_FIRED_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "crossbow_fired"),
				CROSSBOW_FIRED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "crossbow_kills"),
				CROSSBOW_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "special_arrow_kills"),
				SPECIAL_ARROW_KILLS_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "damage_taken"),
				DAMAGE_TAKEN_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "fire_damage_taken"),
				FIRE_DAMAGE_TAKEN_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "explosion_hits"),
				EXPLOSION_HITS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "projectile_hits_taken"),
				PROJECTILE_HITS_TAKEN_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "fall_damage_taken"),
				FALL_DAMAGE_TAKEN_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "mob_melee_hits"),
				MOB_MELEE_HITS_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "underwater_blocks_mined"),
				UNDERWATER_BLOCKS_MINED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "drowning_damage"),
				DROWNING_DAMAGE_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "underwater_travel"),
				UNDERWATER_TRAVEL_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "armored_hits"),
				ARMORED_HITS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "mace_smash_damage"),
				MACE_SMASH_DAMAGE_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "snowy_travel"),
				SNOWY_TRAVEL_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "nether_travel"),
				NETHER_TRAVEL_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "sneak_travel"),
				SNEAK_TRAVEL_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "wind_charges_used"),
				WIND_CHARGES_USED_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "enchantments_unlocked"),
				ENCHANTMENTS_UNLOCKED_TRIGGER
		);

		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "trident_retrievals"),
				TRIDENT_RETRIEVALS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "rain_travel"),
				RAIN_TRAVEL_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "thunderstorm_kills"),
				THUNDERSTORM_KILLS_TRIGGER
		);
		Registry.register(
				BuiltInRegistries.TRIGGER_TYPES,
				Identifier.fromNamespaceAndPath(MOD_ID, "aquatic_kills"),
				AQUATIC_KILLS_TRIGGER
		);

		// Register event handlers
		new BlockBreakHandler().register();
		new MobKillHandler().register();

		// Register version check networking
		PayloadTypeRegistry.playS2C().register(VersionPayload.TYPE, VersionPayload.CODEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayNetworking.send(handler.getPlayer(), new VersionPayload(MOD_VERSION));
		});

		LOGGER.info("Earned Enchants v" + MOD_VERSION + " initialized");
	}
}
