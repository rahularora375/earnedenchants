package com.rahularora.arcaneprogression.events;

import com.rahularora.arcaneprogression.ArcaneProgression;
import com.rahularora.arcaneprogression.data.PlayerDataAttachments;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockBreakHandler {

    public void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                // Total blocks mined
                int blockCount = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.BLOCKS_MINED);
                blockCount++;
                serverPlayer.setAttached(PlayerDataAttachments.BLOCKS_MINED, blockCount);
                ArcaneProgression.BLOCKS_MINED_TRIGGER.trigger(serverPlayer, blockCount);

                // Diamond ore mined (Fortune chain)
                if (state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                    int diamondCount = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.DIAMONDS_MINED);
                    diamondCount++;
                    serverPlayer.setAttached(PlayerDataAttachments.DIAMONDS_MINED, diamondCount);
                    ArcaneProgression.DIAMONDS_MINED_TRIGGER.trigger(serverPlayer, diamondCount);

                    // Track actual diamond items dropped (accounting for Fortune)
                    List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, blockEntity, serverPlayer, serverPlayer.getMainHandItem());
                    int diamondItems = 0;
                    for (ItemStack drop : drops) {
                        if (drop.is(Items.DIAMOND)) {
                            diamondItems += drop.getCount();
                        }
                    }
                    if (diamondItems > 0) {
                        int pickedUp = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.DIAMONDS_PICKED_UP) + diamondItems;
                        serverPlayer.setAttached(PlayerDataAttachments.DIAMONDS_PICKED_UP, pickedUp);
                    }
                }

                // Underwater blocks mined (Aqua Affinity)
                if (serverPlayer.isUnderWater()) {
                    int uwBlocks = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.UNDERWATER_BLOCKS_MINED) + 1;
                    serverPlayer.setAttached(PlayerDataAttachments.UNDERWATER_BLOCKS_MINED, uwBlocks);
                    ArcaneProgression.UNDERWATER_BLOCKS_MINED_TRIGGER.trigger(serverPlayer, uwBlocks);
                }

                // Unique block types mined
                String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                List<String> uniqueBlocks = serverPlayer.getAttachedOrCreate(PlayerDataAttachments.UNIQUE_BLOCKS_MINED);
                if (!uniqueBlocks.contains(blockId)) {
                    List<String> updated = new java.util.ArrayList<>(uniqueBlocks);
                    updated.add(blockId);
                    serverPlayer.setAttached(PlayerDataAttachments.UNIQUE_BLOCKS_MINED, updated);
                    ArcaneProgression.UNIQUE_BLOCKS_MINED_TRIGGER.trigger(serverPlayer, updated.size());
                }
            }
        });
    }
}
