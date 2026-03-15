package com.rahularora.arcaneprogression.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class CountReachedTrigger extends SimpleCriterionTrigger<CountReachedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int count) {
        this.trigger(player, instance -> instance.matches(count));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            int minCount
    ) implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.INT.fieldOf("min_count").forGetter(TriggerInstance::minCount)
        ).apply(i, TriggerInstance::new));

        public boolean matches(int count) {
            return count >= minCount;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }
}
