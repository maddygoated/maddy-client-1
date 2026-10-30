package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingHud extends TextHudModule {
	public PingHud() {
		super("Ping", "Shows your latency to the server.", true, 0.005, 0.16);
	}

	@Override
	protected String label() {
		return "Ping";
	}

	@Override
	protected String value(boolean preview) {
		var mc = mc();
		if (mc.player == null || mc.getConnection() == null) return "0 ms";
		PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
		return (info == null ? 0 : info.getLatency()) + " ms";
	}
}
