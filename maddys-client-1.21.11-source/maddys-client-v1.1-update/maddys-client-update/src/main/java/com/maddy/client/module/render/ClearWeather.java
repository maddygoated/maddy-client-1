package com.maddy.client.module.render;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;

/** Hides rain and thunder on your screen only (client-side). */
public class ClearWeather extends Module {
	public ClearWeather() {
		super("Clear Weather", "Removes rain and thunder on your screen only.", Category.RENDER, false);
	}

	@Override
	public void onTick() {
		var level = mc().level;
		if (level == null) return;
		level.setRainLevel(0f);
		level.setThunderLevel(0f);
	}
}
