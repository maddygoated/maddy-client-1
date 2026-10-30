package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;

public class SaturationHud extends TextHudModule {
	public SaturationHud() {
		super("Saturation", "Shows your hidden saturation and hunger values.", false, 0.005, 0.72);
	}

	@Override
	protected String label() {
		return "Saturation";
	}

	@Override
	protected String value(boolean preview) {
		var p = mc().player;
		if (p == null) return "20.0 (20)";
		return String.format("%.1f (%d)", p.getFoodData().getSaturationLevel(), p.getFoodData().getFoodLevel());
	}
}
