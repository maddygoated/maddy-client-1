package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.tracker.CombatTracker;

public class ComboHud extends TextHudModule {
	public ComboHud() {
		super("Combo Display", "Counts your hits in a row (resets when you get hit).", false, 0.005, 0.62);
	}

	@Override
	protected String label() {
		return "Combo";
	}

	@Override
	protected String value(boolean preview) {
		int c = CombatTracker.combo();
		return c == 0 ? "No combo" : c + " hit" + (c == 1 ? "" : "s");
	}
}
