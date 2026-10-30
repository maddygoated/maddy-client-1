package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.tracker.CpsTracker;

public class CpsHud extends TextHudModule {
	private final BooleanSetting showRight = add(new BooleanSetting("Show Right Click", true));

	public CpsHud() {
		super("CPS", "Shows your clicks per second.", true, 0.005, 0.115);
	}

	@Override
	protected String label() {
		return "CPS";
	}

	@Override
	protected String value(boolean preview) {
		return showRight.isOn() ? CpsTracker.left() + " | " + CpsTracker.right() : String.valueOf(CpsTracker.left());
	}
}
