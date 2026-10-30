package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;

public class BiomeHud extends TextHudModule {
	public BiomeHud() {
		super("Biome", "Shows the biome you are standing in.", false, 0.005, 0.26);
	}

	@Override
	protected String label() {
		return "Biome";
	}

	@Override
	protected String value(boolean preview) {
		var p = mc().player;
		var level = mc().level;
		if (p == null || level == null) return "Plains";
		String id = level.getBiome(p.blockPosition()).getRegisteredName();
		int colon = id.indexOf(':');
		if (colon >= 0) id = id.substring(colon + 1);
		StringBuilder sb = new StringBuilder();
		for (String w : id.split("_")) {
			if (w.isEmpty()) continue;
			if (sb.length() > 0) sb.append(' ');
			sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
		}
		return sb.toString();
	}
}
