package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class DamageTakenMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void onDamageTaken(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ServerPlayer player)) return;

        // Total damage taken (Protection)
        int totalDmg = player.getAttachedOrCreate(PlayerDataAttachments.DAMAGE_TAKEN) + (int) amount;
        player.setAttached(PlayerDataAttachments.DAMAGE_TAKEN, totalDmg);
        ArcaneProgression.DAMAGE_TAKEN_TRIGGER.trigger(player, totalDmg);

        // Fire damage taken in the Nether (Fire Protection)
        if (source.is(DamageTypeTags.IS_FIRE) && level.dimension() == Level.NETHER) {
            int fireDmg = player.getAttachedOrCreate(PlayerDataAttachments.NETHER_FIRE_DAMAGE) + (int) amount;
            player.setAttached(PlayerDataAttachments.NETHER_FIRE_DAMAGE, fireDmg);
            ArcaneProgression.NETHER_FIRE_DAMAGE_TRIGGER.trigger(player, fireDmg);
        }

        // Explosion hits (Blast Protection)
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            int explosionHits = player.getAttachedOrCreate(PlayerDataAttachments.EXPLOSION_HITS) + 1;
            player.setAttached(PlayerDataAttachments.EXPLOSION_HITS, explosionHits);
            ArcaneProgression.EXPLOSION_HITS_TRIGGER.trigger(player, explosionHits);
        }

        // Projectile hits (Projectile Protection)
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            int projectileHits = player.getAttachedOrCreate(PlayerDataAttachments.PROJECTILE_HITS_TAKEN) + 1;
            player.setAttached(PlayerDataAttachments.PROJECTILE_HITS_TAKEN, projectileHits);
            ArcaneProgression.PROJECTILE_HITS_TAKEN_TRIGGER.trigger(player, projectileHits);
        }

        // Fall damage (Feather Falling)
        if (source.is(DamageTypeTags.IS_FALL)) {
            int fallDmg = player.getAttachedOrCreate(PlayerDataAttachments.FALL_DAMAGE_TAKEN) + (int) amount;
            player.setAttached(PlayerDataAttachments.FALL_DAMAGE_TAKEN, fallDmg);
            ArcaneProgression.FALL_DAMAGE_TAKEN_TRIGGER.trigger(player, fallDmg);
        }

        // Mob melee hits (Thorns)
        if (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) {
            int meleeHits = player.getAttachedOrCreate(PlayerDataAttachments.MOB_MELEE_HITS) + 1;
            player.setAttached(PlayerDataAttachments.MOB_MELEE_HITS, meleeHits);
            ArcaneProgression.MOB_MELEE_HITS_TRIGGER.trigger(player, meleeHits);
        }

        // Drowning damage (Respiration)
        if (source.is(DamageTypes.DROWN)) {
            int drownDmg = player.getAttachedOrCreate(PlayerDataAttachments.DROWNING_DAMAGE) + (int) amount;
            player.setAttached(PlayerDataAttachments.DROWNING_DAMAGE, drownDmg);
            ArcaneProgression.DROWNING_DAMAGE_TRIGGER.trigger(player, drownDmg);
        }
    }
}