package com.maddy.client.module.render;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.NumberSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;

/** Pulsing red screen edges when your health is low. */
public class LowHealthWarning extends Module {
	private final NumberSetting threshold = add(new NumberSetting("Warn Below", 6, 2, 14, 1, " HP"));
	private final NumberSetting intensity = add(new NumberSetting("Intensity", 0.6, 0.2, 1.0, 0.05));

	public LowHealthWarning() {
		super("Low HP Warning", "Red glowing screen edges when your health is low.", Category.RENDER, true);
	}

	public void render(GuiGraphics g) {
		var p = mc().player;
		if (p == null || p.isCreative() || p.isSpectator()) return;
		float hp = p.getHealth();
		if (hp <= 0 || hp > threshold.getFloat()) return;
		float danger = 1f - hp / threshold.getFloat();
		float pulse = 0.65f + 0.35f * (float) Math.sin(System.currentTimeMillis() / 160.0);
		int a = (int) (255 * Render2D.clamp01((0.35f + danger * 0.65f) * pulse * intensity.getFloat()));
		int red = (a << 24) | 0xE11D48;
		int none = 0x00E11D48;
		int w = g.guiWidth(), h = g.guiHeight();
		int size = Math.max(20, h / 6);
		g.fillGradient(0, 0, w, size, red, none);
		g.fillGradient(0, h - size, w, h, none, red);
		for (int i = 0; i < size; i += 2) {
			int c = Render2D.withAlpha(0xE11D48, (int) (a * (1f - (float) i / size)));
			g.fill(i, 0, i + 2, h, c);
			g.fill(w - i - 2, 0, w - i, h, c);
		}
	}
}
