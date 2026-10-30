package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.tracker.SessionTracker;

public class PlaytimeHud extends TextHudModule {
	public PlaytimeHud() {
		super("Playtime", "How long you have been in this world or server.", false, 0.005, 0.42);
	}

	@Override
	protected String label() {
		return "Session";
	}

	@Override
	protected String value(boolean preview) {
		return SessionTracker.format(SessionTracker.millis());
	}
}
