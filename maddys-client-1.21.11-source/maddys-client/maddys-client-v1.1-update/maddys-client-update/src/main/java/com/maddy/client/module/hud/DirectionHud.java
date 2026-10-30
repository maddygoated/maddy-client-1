package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** A horizontal compass strip like the ones in modern PvP clients. */
public class DirectionHud extends HudModule {
	private static final String[] LABELS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

	public DirectionHud() {
		super("Direction", "Compass strip showing which way you face.", false, 0.42, 0.01);
	}

	@Override
	protected void layout(boolean preview) {
		width = 160;
		height = 22;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		float yaw = mc().player == null ? 0 : mc().player.getYRot();
		yaw = ((yaw % 360) + 360) % 360; // 0 = south
		int center = width / 2;
		float pxPerDeg = 1.4f;
		int half = (int) ((width / 2f - 6) / pxPerDeg);

		for (int deg = -half - 15; deg <= half + 15; deg++) {
			int world = Math.round(yaw) + deg;
			int norm = ((world % 360) + 360) % 360;
			int x = center + Math.round(deg * pxPerDeg);
			if (x < 4 || x > width - 4) continue;
			if (norm % 45 == 0) {
				String label = LABELS[norm / 45];
				boolean cardinal = label.length() == 1;
				int col = cardinal ? (label.equals("N") ? accentColor.getColor() : textColor.getColor()) : Render2D.alpha(textColor.getColor(), 0.7f);
				Render2D.centered(g, font, label, x + 1, 4, col, shadow());
			} else if (norm % 15 == 0) {
				Render2D.rect(g, x, 8, 1, 4, Render2D.alpha(textColor.getColor(), 0.35f));
			}
		}
		Render2D.rect(g, center, 15, 1, 5, accentColor.getColor());
		String deg = Math.round(yaw) + "\u00B0";
		Render2D.scaledText(g, font, deg, center + 4, 15, 0.6f, Render2D.alpha(textColor.getColor(), 0.8f), false);
	}
}
