package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;

/** In-game time of day as a clock. */
public class GameTimeHud extends TextHudModule {
	public GameTimeHud() {
		super("Game Time", "In-game time of day (great for beds and mobs).", false, 0.005, 0.36);
	}

	@Override
	protected String label() {
		return "Game Time";
	}

	@Override
	protected String value(boolean preview) {
		var level = mc().level;
		long t = level == null ? 6000 : level.getDayTime() % 24000L;
		long hours = (t / 1000 + 6) % 24;
		long minutes = (t % 1000) * 60 / 1000;
		String phase = t < 12000 ? "Day" : (t < 13800 ? "Sunset" : (t < 22200 ? "Night" : "Sunrise"));
		return String.format("%02d:%02d %s", hours, minutes, phase);
	}
}
