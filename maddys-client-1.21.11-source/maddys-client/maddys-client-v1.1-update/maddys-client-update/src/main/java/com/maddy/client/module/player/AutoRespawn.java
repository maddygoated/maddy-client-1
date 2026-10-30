package com.maddy.client.module.player;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.NumberSetting;
import net.minecraft.client.gui.screens.DeathScreen;

/** Respawns automatically after you die. */
public class AutoRespawn extends Module {
	private final NumberSetting delay = add(new NumberSetting("Delay", 1.0, 0.0, 5.0, 0.5, "s"));
	private long deathScreenAt = -1;

	public AutoRespawn() {
		super("Auto Respawn", "Skips the death screen and respawns you.", Category.PLAYER, false);
	}

	@Override
	public void onTick() {
		var mc = mc();
		if (mc.player == null) return;
		if (mc.screen instanceof DeathScreen) {
			long now = System.currentTimeMillis();
			if (deathScreenAt < 0) deathScreenAt = now;
			if (now - deathScreenAt >= (long) (delay.get() * 1000)) {
				mc.player.respawn();
				mc.setScreen(null);
				deathScreenAt = -1;
			}
		} else {
			deathScreenAt = -1;
		}
	}
}
