package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import net.minecraft.world.level.LightLayer;

/** Block light at your feet - 0 means mobs can spawn. */
public class LightLevelHud extends TextHudModule {
	public LightLevelHud() {
		super("Light Level", "Block light at your feet (0 = mobs can spawn).", false, 0.005, 0.31);
	}

	@Override
	protected String label() {
		return "Light";
	}

	@Override
	protected String value(boolean preview) {
		var p = mc().player;
		var level = mc().level;
		if (p == null || level == null) return "15";
		int block = level.getBrightness(LightLayer.BLOCK, p.blockPosition());
		int sky = level.getBrightness(LightLayer.SKY, p.blockPosition());
		return block + (block == 0 ? " (spawnable)" : "") + "  sky " + sky;
	}
}
