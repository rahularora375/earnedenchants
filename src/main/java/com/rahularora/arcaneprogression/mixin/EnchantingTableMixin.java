package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.data.EnchantmentUnlockData;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentMenu.class)
public class EnchantingTableMixin {

    @Unique
    private Player arcane$player;

    // Capture the player from the Inventory passed to the constructor
    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    private void arcane$capturePlayer(int containerId, Inventory inventory, ContainerLevelAccess access, CallbackInfo ci) {
        this.arcane$player = inventory.player;
    }

    // Set ThreadLocal at start of getEnchantmentList so EnchantmentHelperMixin can read it
    @Inject(method = "getEnchantmentList", at = @At("HEAD"))
    private void arcane$setEnchantingPlayer(RegistryAccess registryAccess, ItemStack stack, int slot, int cost,
                                             CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (arcane$player instanceof ServerPlayer sp) {
            EnchantmentUnlockData.ENCHANTING_PLAYER.set(sp);
        }
    }

    // Pre-filter locked enchantments from the stream and add unlocked treasure enchantments
    @ModifyArg(
            method = "getEnchantmentList",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;"),
            index = 3
    )
    private Stream<Holder<Enchantment>> arcane$filterAndAddTreasure(Stream<Holder<Enchantment>> original) {
        if (!(arcane$player instanceof ServerPlayer serverPlayer)) {
            return original;
        }

        // Filter out fully locked enchantments
        Stream<Holder<Enchantment>> filtered = original.filter(holder -> {
            ResourceKey<Enchantment> key = holder.unwrapKey().orElse(null);
            if (key == null) return true;
            return EnchantmentUnlockData.getMaxUnlockedLevel(serverPlayer, key) > 0;
        });

        // Add unlocked treasure enchantments
        var registry = serverPlayer.level().getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        List<Holder<Enchantment>> treasureHolders = EnchantmentUnlockData.getUnlockedTreasureHolders(serverPlayer, registry);
        if (!treasureHolders.isEmpty()) {
            return Stream.concat(filtered, treasureHolders.stream());
        }
        return filtered;
    }

    // Clear ThreadLocal at end of getEnchantmentList
    @Inject(method = "getEnchantmentList", at = @At("RETURN"))
    private void arcane$clearEnchantingPlayer(RegistryAccess registryAccess, ItemStack stack, int slot, int cost,
                                               CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        EnchantmentUnlockData.ENCHANTING_PLAYER.remove();
    }
}
