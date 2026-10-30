package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import com.maddy.client.module.setting.ModeSetting;

public class SpeedHud extends TextHudModule {
	private final ModeSetting unit = add(new ModeSetting("Unit", "m/s", "m/s", "km/h"));
	private double lastX, lastZ;
	private boolean has;
	private double speed;

	public SpeedHud() {
		super("Speed", "Shows how fast you are moving.", false, 0.005, 0.32);
	}

	@Override
	public void onTick() {
		var p = mc().player;
		if (p == null) {
			has = false;
			return;
		}
		if (has) {
			double dx = p.getX() - lastX;
			double dz = p.getZ() - lastZ;
			double now = Math.sqrt(dx * dx + dz * dz) * 20.0;
			speed = speed * 0.6 + now * 0.4;
		}
		lastX = p.getX();
		lastZ = p.getZ();
		has = true;
	}

	@Override
	protected String label() {
		return "Speed";
	}

	@Override
	protected String value(boolean preview) {
		double v = unit.is("km/h") ? speed * 3.6 : speed;
		return String.format("%.2f %s", v, unit.get());
	}
}
