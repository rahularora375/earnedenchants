package com.rahularora.arcaneprogression.mixin;

import com.rahularora.arcaneprogression.ArcaneProgression;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(AdvancementVisibilityEvaluator.class)
public class AdvancementVisibilityMixin {

    @ModifyArg(
            method = "evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator;evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)Z"
            ),
            index = 3
    )
    private static AdvancementVisibilityEvaluator.Output wrapOutput(AdvancementVisibilityEvaluator.Output output) {
        return (node, visible) -> {
            if (node.holder().id().getNamespace().equals(ArcaneProgression.MOD_ID)) {
                output.accept(node, true);
            } else {
                output.accept(node, visible);
            }
        };
    }
}
