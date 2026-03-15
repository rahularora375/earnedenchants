package com.rahularora.arcaneprogression.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class BlocksMinedTrigger extends SimpleCriterionTrigger<BlocksMinedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int blocksMined) {
        this.trigger(player, instance -> instance.matches(blocksMined));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            int minBlocks
    ) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.INT.fieldOf("min_blocks").forGetter(TriggerInstance::minBlocks)
        ).apply(i, TriggerInstance::new));

        public boolean matches(int blocksMined) {
            return blocksMined >= minBlocks;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }
}
