package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;

public class FpsHud extends TextHudModule {
	public FpsHud() {
		super("FPS", "Shows your frames per second.", true, 0.005, 0.07);
	}

	@Override
	protected String label() {
		return "FPS";
	}

	@Override
	protected String value(boolean preview) {
		return String.valueOf(mc().getFps());
	}
}
