package com.maddy.client.module.render;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.ColorSetting;
import com.maddy.client.render.Render2D;
import com.maddy.client.tracker.CombatTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Flashes an X around the crosshair when you land a hit. */
public class HitMarker extends Module {
	private final ColorSetting color = add(new ColorSetting("Color", 0xFFFF4F9A));

	public HitMarker() {
		super("Hit Indicator", "Shows a hit marker when you land a hit.", Category.RENDER, false);
	}

	public void render(GuiGraphics g) {
		long since = System.currentTimeMillis() - CombatTracker.lastHitTime();
		if (since > 300) return;
		float a = 1f - since / 300f;
		int c = Render2D.alpha(color.getColor(), a);
		int cx = g.guiWidth() / 2;
		int cy = g.guiHeight() / 2;
		for (int i = 3; i <= 6; i++) {
			Render2D.rect(g, cx - i, cy - i, 1, 1, c);
			Render2D.rect(g, cx + i, cy - i, 1, 1, c);
			Render2D.rect(g, cx - i, cy + i, 1, 1, c);
			Render2D.rect(g, cx + i, cy + i, 1, 1, c);
		}
	}
}
