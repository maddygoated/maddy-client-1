package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.module.setting.ModeSetting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockHud extends TextHudModule {
	private static final DateTimeFormatter H12 = DateTimeFormatter.ofPattern("hh:mm a");
	private static final DateTimeFormatter H24 = DateTimeFormatter.ofPattern("HH:mm");
	private final ModeSetting format = add(new ModeSetting("Format", "12h", "12h", "24h"));

	public ClockHud() {
		super("Clock", "Shows the real-world time.", false, 0.005, 0.37);
	}

	@Override
	protected String label() {
		return "Time";
	}

	@Override
	protected String value(boolean preview) {
		return LocalTime.now().format(format.is("24h") ? H24 : H12);
	}
}
