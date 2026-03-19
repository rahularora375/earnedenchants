package com.rahularora.arcaneprogression.network;

import com.rahularora.arcaneprogression.ArcaneProgression;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record VersionPayload(String version) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VersionPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ArcaneProgression.MOD_ID, "version"));

    public static final StreamCodec<ByteBuf, VersionPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, VersionPayload::version,
                    VersionPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}