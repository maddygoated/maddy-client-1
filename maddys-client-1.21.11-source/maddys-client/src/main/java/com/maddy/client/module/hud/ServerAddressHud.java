package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import net.minecraft.client.multiplayer.ServerData;

public class ServerAddressHud extends TextHudModule {
	public ServerAddressHud() {
		super("Server Address", "Shows the server you are playing on.", false, 0.005, 0.52);
	}

	@Override
	protected String label() {
		return "IP";
	}

	@Override
	protected String value(boolean preview) {
		var mc = mc();
		if (mc.isLocalServer()) return "Singleplayer";
		ServerData data = mc.getCurrentServer();
		return data == null ? (preview ? "play.example.net" : "Offline") : data.ip;
	}
}
