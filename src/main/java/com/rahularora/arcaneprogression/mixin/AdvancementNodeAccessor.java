package com.rahularora.arcaneprogression.mixin;

import net.minecraft.advancements.AdvancementNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(AdvancementNode.class)
public interface AdvancementNodeAccessor {

    @Accessor("children")
    Set<AdvancementNode> getChildren();

    @Mutable
    @Accessor("children")
    void setChildren(Set<AdvancementNode> children);
}