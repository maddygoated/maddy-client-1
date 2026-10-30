package com.maddy.client.module.player;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;

/** Keeps the sprint key held so you never have to hold it yourself. */
public class ToggleSprint extends Module {
	public ToggleSprint() {
		super("Toggle Sprint", "Always sprint without holding the sprint key.", Category.PLAYER, false);
	}

	@Override
	public void onTick() {
		if (mc().player == null || mc().screen != null) return;
		mc().options.keySprint.setDown(true);
	}

	@Override
	public void onDisable() {
		if (mc().options != null) mc().options.keySprint.setDown(false);
	}
}
