package com.maddy.client.module.render;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ColorSetting;
import com.maddy.client.module.setting.ModeSetting;
import com.maddy.client.module.setting.NumberSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

/** Replaces the vanilla crosshair with a configurable one. */
public class CustomCrosshair extends Module {
	private final ModeSetting shape = add(new ModeSetting("Shape", "Cross", "Cross", "Dot", "Circle", "Cross + Dot", "T-Shape", "Square"));
	private final NumberSetting size = add(new NumberSetting("Size", 5, 1, 15, 1));
	private final NumberSetting gap = add(new NumberSetting("Gap", 2, 0, 10, 1));
	private final NumberSetting thickness = add(new NumberSetting("Thickness", 1, 1, 4, 1));
	private final ColorSetting color = add(new ColorSetting("Color", 0xFFFFFFFF));
	private final BooleanSetting outline = add(new BooleanSetting("Outline", true));
	private final BooleanSetting targetColor = add(new BooleanSetting("Red On Target", true));

	public CustomCrosshair() {
		super("Custom Crosshair", "Pick your own crosshair shape, size and colour.", Category.RENDER, false);
	}

	public void render(GuiGraphics g) {
		int cx = g.guiWidth() / 2;
		int cy = g.guiHeight() / 2;
		int col = color.getColor();
		if (targetColor.isOn() && mc().crosshairPickEntity instanceof LivingEntity) col = 0xFFFF4D4D;
		int s = size.getInt();
		int gp = gap.getInt();
		int t = thickness.getInt();
		int o = 0xC0000000;

		switch (shape.get()) {
			case "Dot" -> dot(g, cx, cy, t + 1, col, o);
			case "Circle" -> {
				if (outline.isOn()) Render2D.ring(g, cx, cy, s + 1, t + 2, o);
				Render2D.ring(g, cx, cy, s, t, col);
			}
			case "Cross + Dot" -> {
				cross(g, cx, cy, s, gp, t, col, o, true);
				dot(g, cx, cy, t, col, o);
			}
			case "T-Shape" -> cross(g, cx, cy, s, gp, t, col, o, false);
			case "Square" -> {
				int half = s;
				if (outline.isOn()) Render2D.outline(g, cx - half - 1, cy - half - 1, half * 2 + 3, half * 2 + 3, o);
				for (int i = 0; i < t; i++) Render2D.outline(g, cx - half + i, cy - half + i, half * 2 + 1 - i * 2, half * 2 + 1 - i * 2, col);
			}
			default -> cross(g, cx, cy, s, gp, t, col, o, true);
		}
	}

	private void dot(GuiGraphics g, int cx, int cy, int r, int col, int o) {
		int x = cx - r / 2;
		int y = cy - r / 2;
		if (outline.isOn()) Render2D.rect(g, x - 1, y - 1, r + 2, r + 2, o);
		Render2D.rect(g, x, y, Math.max(1, r), Math.max(1, r), col);
	}

	private void cross(GuiGraphics g, int cx, int cy, int s, int gp, int t, int col, int o, boolean top) {
		int h = t / 2;
		// rectangles: x, y, w, h
		int[][] bars = {
				{cx - gp - s, cy - h, s, t}, // left
				{cx + gp + 1, cy - h, s, t}, // right
				{cx - h, cy + gp + 1, t, s}, // bottom
				{cx - h, cy - gp - s, t, s}  // top
		};
		int n = top ? 4 : 3;
		if (outline.isOn()) for (int i = 0; i < n; i++) Render2D.rect(g, bars[i][0] - 1, bars[i][1] - 1, bars[i][2] + 2, bars[i][3] + 2, o);
		for (int i = 0; i < n; i++) Render2D.rect(g, bars[i][0], bars[i][1], bars[i][2], bars[i][3], col);
	}
}
