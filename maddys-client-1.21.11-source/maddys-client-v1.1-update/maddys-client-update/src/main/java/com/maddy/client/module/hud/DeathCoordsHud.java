package com.maddy.client.module.hud;

import com.maddy.client.module.TextHudModule;
import net.minecraft.network.chat.Component;

/** Remembers where you last died and tells you in chat. */
public class DeathCoordsHud extends TextHudModule {
	private boolean wasDead;
	private String last = null;

	public DeathCoordsHud() {
		super("Death Coordinates", "Remembers where you last died and posts it in chat.", true, 0.005, 0.41);
	}

	@Override
	public void onTickAlways() {
		var p = mc().player;
		if (p == null) return;
		boolean dead = p.isDeadOrDying();
		if (dead && !wasDead) {
			String dim = mc().level == null ? "" : mc().level.dimension().toString();
			dim = dim.contains("nether") ? " (Nether)" : dim.contains("the_end") ? " (End)" : "";
			last = (int) Math.floor(p.getX()) + ", " + (int) Math.floor(p.getY()) + ", " + (int) Math.floor(p.getZ()) + dim;
			if (isEnabled()) p.displayClientMessage(Component.literal("\u00a7d[Maddy's Client] \u00a7fYou died at \u00a7d" + last), false);
		}
		wasDead = dead;
	}

	@Override
	protected boolean shouldRender() {
		return last != null;
	}

	@Override
	protected String label() {
		return "Last Death";
	}

	@Override
	protected String value(boolean preview) {
		return last == null ? "120, 64, -340" : last;
	}
}
