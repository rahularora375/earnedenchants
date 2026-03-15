package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MeleeHitMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void onMeleeHit(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!(source.getEntity() instanceof ServerPlayer player)) return;

        LivingEntity target = (LivingEntity) (Object) this;

        // Armored hits (Breach) — any player-caused damage, target has armor
        if (target.getArmorValue() > 2) {
            int armoredHits = player.getAttachedOrCreate(PlayerDataAttachments.ARMORED_HITS);
            armoredHits++;
            player.setAttached(PlayerDataAttachments.ARMORED_HITS, armoredHits);
            ArcaneProgression.ARMORED_HITS_TRIGGER.trigger(player, armoredHits);
        }

        // Mace smash damage (Density) — mace + falling smash attack
        if (source.getDirectEntity() == player
                && player.getMainHandItem().getItem() instanceof MaceItem
                && MaceItem.canSmashAttack(player)) {
            int smashDamage = player.getAttachedOrCreate(PlayerDataAttachments.MACE_SMASH_DAMAGE);
            smashDamage += (int) amount;
            player.setAttached(PlayerDataAttachments.MACE_SMASH_DAMAGE, smashDamage);
            ArcaneProgression.MACE_SMASH_DAMAGE_TRIGGER.trigger(player, smashDamage);
        }
    }
}