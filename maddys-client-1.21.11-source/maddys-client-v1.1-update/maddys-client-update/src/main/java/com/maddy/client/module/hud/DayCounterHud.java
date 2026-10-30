package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;

public class DayCounterHud extends TextHudModule {
	public DayCounterHud() {
		super("Day Counter", "Shows the current in-game day.", false, 0.005, 0.77);
	}

	@Override
	protected String label() {
		return "Day";
	}

	@Override
	protected String value(boolean preview) {
		var level = mc().level;
		return level == null ? "1" : String.valueOf(level.getDayTime() / 24000L + 1);
	}
}
