package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.module.setting.ModeSetting;

public class MemoryHud extends TextHudModule {
	private final ModeSetting display = add(new ModeSetting("Display", "Used / Max", "Used / Max", "Percent"));

	public MemoryHud() {
		super("Memory", "Shows Java memory usage (system resources).", false, 0.005, 0.57);
	}

	@Override
	protected String label() {
		return "RAM";
	}

	@Override
	protected String value(boolean preview) {
		Runtime rt = Runtime.getRuntime();
		long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
		long max = rt.maxMemory() / 1048576L;
		if (display.is("Percent")) return (max == 0 ? 0 : used * 100 / max) + "%";
		return used + " / " + max + " MB";
	}
}
