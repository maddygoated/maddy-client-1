package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Your health as a number with a colour bar (includes absorption). */
public class HealthHud extends HudModule {
	private float shown = 1f;

	public HealthHud() {
		super("Health Display", "Your exact health and absorption with a colour bar.", false, 0.42, 0.80);
	}

	@Override
	protected void layout(boolean preview) {
		width = 92;
		height = 22;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		var p = mc().player;
		float hp = p == null ? 17 : p.getHealth();
		float max = p == null ? 20 : Math.max(1, p.getMaxHealth());
		float abs = p == null ? 0 : p.getAbsorptionAmount();
		float pct = Render2D.clamp01(hp / max);
		shown = Render2D.approach(shown, pct, 0.2f);
		drawBackground(g, 0, 0, width, height);
		String t = String.format("\u2764 %.1f", hp) + (abs > 0 ? String.format(" +%.0f", abs) : "") + " / " + Math.round(max);
		g.drawString(font, t, 5, 4, abs > 0 ? 0xFFFACC15 : textColor.getColor(), shadow());
		Render2D.round(g, 5, 15, width - 10, 3, 1, 0x55000000);
		Render2D.round(g, 5, 15, Math.max(2, (int) ((width - 10) * shown)), 3, 1, Render2D.healthColor(pct));
	}
}
