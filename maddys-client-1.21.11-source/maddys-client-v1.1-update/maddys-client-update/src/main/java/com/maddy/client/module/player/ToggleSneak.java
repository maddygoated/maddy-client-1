package com.maddy.client.module.player;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;

public class ToggleSneak extends Module {
	public ToggleSneak() {
		super("Toggle Sneak", "Stay sneaking without holding the sneak key.", Category.PLAYER, false);
	}

	@Override
	public void onTick() {
		if (mc().player == null || mc().screen != null) return;
		mc().options.keyShift.setDown(true);
	}

	@Override
	public void onDisable() {
		if (mc().options != null) mc().options.keyShift.setDown(false);
	}
}
