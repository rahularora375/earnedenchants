package com.rahularora.arcaneprogression.events;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;

public class MobKillHandler {

    public void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killed, source) -> {
            if (killer instanceof ServerPlayer serverPlayer) {
                // Total mob kills (Sharpness)
                int totalKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.MOB_KILLS);
                totalKills++;
                serverPlayer.setAttached(PlayerDataAttachments.MOB_KILLS, totalKills);
                ArcaneProgression.MOB_KILLS_TRIGGER.trigger(serverPlayer, totalKills);

                // Undead kills (Smite)
                if (killed.getType().is(EntityTypeTags.UNDEAD)) {
                    int undeadKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.UNDEAD_KILLS);
                    undeadKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.UNDEAD_KILLS, undeadKills);
                    ArcaneProgression.UNDEAD_KILLS_TRIGGER.trigger(serverPlayer, undeadKills);
                }

                // Arthropod kills (Bane of Arthropods)
                if (killed.getType().is(EntityTypeTags.ARTHROPOD)) {
                    int arthropodKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.ARTHROPOD_KILLS);
                    arthropodKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.ARTHROPOD_KILLS, arthropodKills);
                    ArcaneProgression.ARTHROPOD_KILLS_TRIGGER.trigger(serverPlayer, arthropodKills);
                }

                // Kills of burning mobs (Fire Aspect)
                if (killed.isOnFire()) {
                    int fireKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.FIRE_KILLS);
                    fireKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.FIRE_KILLS, fireKills);
                    ArcaneProgression.FIRE_KILLS_TRIGGER.trigger(serverPlayer, fireKills);
                }

                // Spear kills (Lunge)
                if (serverPlayer.getMainHandItem().is(ItemTags.SPEARS)) {
                    int spearKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.SPEAR_KILLS);
                    spearKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.SPEAR_KILLS, spearKills);
                    ArcaneProgression.SPEAR_KILLS_TRIGGER.trigger(serverPlayer, spearKills);
                }

                // Nether bow kills (Flame)
                if (source.getDirectEntity() instanceof AbstractArrow
                        && world.dimension() == Level.NETHER) {
                    int netherBowKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.NETHER_BOW_KILLS);
                    netherBowKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.NETHER_BOW_KILLS, netherBowKills);
                    ArcaneProgression.NETHER_BOW_KILLS_TRIGGER.trigger(serverPlayer, netherBowKills);
                }

                // Crossbow kills (Piercing)
                if (source.getDirectEntity() instanceof AbstractArrow arrow
                        && arrow.getWeaponItem().is(Items.CROSSBOW)) {
                    int crossbowKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.CROSSBOW_KILLS);
                    crossbowKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.CROSSBOW_KILLS, crossbowKills);
                    ArcaneProgression.CROSSBOW_KILLS_TRIGGER.trigger(serverPlayer, crossbowKills);
                }

                // Special arrow kills (Multishot) — spectral, tipped, or firework
                boolean isSpecialArrow = source.getDirectEntity() instanceof SpectralArrow
                        || (source.getDirectEntity() instanceof Arrow a && a.getColor() != -1)
                        || source.getDirectEntity() instanceof FireworkRocketEntity;
                if (isSpecialArrow) {
                    int specialKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.SPECIAL_ARROW_KILLS);
                    specialKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.SPECIAL_ARROW_KILLS, specialKills);
                    ArcaneProgression.SPECIAL_ARROW_KILLS_TRIGGER.trigger(serverPlayer, specialKills);
                }

                // Thunderstorm kills (Channeling)
                if (world.isThundering()) {
                    int thunderKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.THUNDERSTORM_KILLS);
                    thunderKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.THUNDERSTORM_KILLS, thunderKills);
                    ArcaneProgression.THUNDERSTORM_KILLS_TRIGGER.trigger(serverPlayer, thunderKills);
                }

                // Aquatic kills (Impaling)
                if (killed.getType().is(EntityTypeTags.SENSITIVE_TO_IMPALING)) {
                    int aquaticKills = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.AQUATIC_KILLS);
                    aquaticKills++;
                    serverPlayer.setAttached(PlayerDataAttachments.AQUATIC_KILLS, aquaticKills);
                    ArcaneProgression.AQUATIC_KILLS_TRIGGER.trigger(serverPlayer, aquaticKills);
                }

                // Unique mob types killed (Looting)
                String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
                List<String> uniqueMobs = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.UNIQUE_MOBS_KILLED);
                if (!uniqueMobs.contains(mobId)) {
                    List<String> updated = new java.util.ArrayList<>(uniqueMobs);
                    updated.add(mobId);
                    serverPlayer.setAttached(PlayerDataAttachments.UNIQUE_MOBS_KILLED, updated);
                    ArcaneProgression.UNIQUE_MOBS_KILLED_TRIGGER.trigger(serverPlayer, updated.size());
                }
            }
        });
    }
}