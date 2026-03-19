package com.rahularora.arcaneprogression;

import com.rahularora.arcaneprogression.network.VersionPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;

public class ArcaneProgressionClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(VersionPayload.TYPE, (payload, context) -> {
			String serverVersion = payload.version();
			String clientVersion = ArcaneProgression.MOD_VERSION;
			if (!serverVersion.equals(clientVersion)) {
				context.client().execute(() -> {
					context.player().connection.getConnection().disconnect(
							Component.literal("Earned Enchants version mismatch! Server: " + serverVersion + ", Client: " + clientVersion)
					);
				});
			}
		});
	}
}