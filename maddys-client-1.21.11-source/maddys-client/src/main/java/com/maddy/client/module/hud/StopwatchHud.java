package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;

public class StopwatchHud extends TextHudModule {
	private long startedAt = -1;
	private long elapsed;

	public StopwatchHud() {
		super("Stopwatch", "A stopwatch - start / stop it with its keybind.", false, 0.005, 0.47);
	}

	public void startStop() {
		if (startedAt < 0) {
			startedAt = System.currentTimeMillis() - elapsed;
		} else {
			elapsed = System.currentTimeMillis() - startedAt;
			startedAt = -1;
		}
	}

	public void reset() {
		startedAt = -1;
		elapsed = 0;
	}

	@Override
	protected String label() {
		return "Stopwatch";
	}

	@Override
	protected String value(boolean preview) {
		long ms = startedAt >= 0 ? System.currentTimeMillis() - startedAt : elapsed;
		long m = ms / 60000;
		long s = (ms / 1000) % 60;
		long cs = (ms / 10) % 100;
		return String.format("%02d:%02d.%02d", m, s, cs);
	}
}
