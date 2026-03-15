package com.rahularora.arcaneprogression.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.TreeNodePosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(TreeNodePosition.class)
public class AdvancementSpacingMixin {

    private static final float CATEGORY_GAP = 1.0f;

    private static final List<String> CATEGORY_ORDER = List.of(
            "tool_cat", "fishing_cat", "armor_cat", "melee_cat",
            "trident_cat", "mace_cat", "bow_cat", "crossbow_cat", "treasure_cat"
    );

    private static final Map<String, Integer> CATEGORY_INDEX;
    static {
        var map = new java.util.HashMap<String, Integer>();
        for (int i = 0; i < CATEGORY_ORDER.size(); i++) {
            map.put("earnedenchants:tool/" + CATEGORY_ORDER.get(i), i);
        }
        CATEGORY_INDEX = Map.copyOf(map);
    }

    @Inject(method = "run", at = @At("HEAD"))
    private static void sortChildren(AdvancementNode root, CallbackInfo ci) {
        if (!root.holder().id().getNamespace().equals("earnedenchants")) return;
        sortChildrenRecursive(root);
    }

    private static void sortChildrenRecursive(AdvancementNode node) {
        var accessor = (AdvancementNodeAccessor) node;
        var sorted = accessor.getChildren().stream()
                .sorted(Comparator.comparingInt(n -> CATEGORY_INDEX.getOrDefault(
                        n.holder().id().toString(), Integer.MAX_VALUE)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        accessor.setChildren(sorted);
        for (AdvancementNode child : sorted) {
            sortChildrenRecursive(child);
        }
    }

    @Inject(method = "run", at = @At("TAIL"))
    private static void addCategorySpacing(AdvancementNode root, CallbackInfo ci) {
        float offset = 0;
        boolean first = true;
        for (AdvancementNode child : root.children()) {
            if (child.advancement().display().isEmpty()) continue;
            if (!first) {
                offset += CATEGORY_GAP;
            }
            first = false;
            if (offset > 0) {
                shiftSubtree(child, offset);
            }
        }
    }

    private static void shiftSubtree(AdvancementNode node, float yOffset) {
        node.advancement().display().ifPresent(d -> d.setLocation(d.getX(), d.getY() + yOffset));
        for (AdvancementNode child : node.children()) {
            shiftSubtree(child, yOffset);
        }
    }
}