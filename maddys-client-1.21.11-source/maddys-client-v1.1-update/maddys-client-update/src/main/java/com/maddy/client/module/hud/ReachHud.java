package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.tracker.CombatTracker;

public class ReachHud extends TextHudModule {
	public ReachHud() {
		super("Reach Display", "Shows the distance of your last hit.", false, 0.005, 0.67);
	}

	@Override
	protected String label() {
		return "Reach";
	}

	@Override
	protected String value(boolean preview) {
		double r = CombatTracker.reach();
		if (r < 0 || System.currentTimeMillis() - CombatTracker.lastHitTime() > 3000) return "-";
		return String.format("%.2f blocks", r);
	}
}
