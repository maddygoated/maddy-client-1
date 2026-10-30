package com.maddy.client.module;

import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ColorSetting;
import com.maddy.client.module.setting.NumberSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** A module that draws a movable, scalable element on the HUD. */
public abstract class HudModule extends Module {
	public final NumberSetting scale = add(new NumberSetting("Scale", 1.0, 0.5, 2.5, 0.05, "x"));
	public final BooleanSetting background = add(new BooleanSetting("Background", true));
	public final ColorSetting backgroundColor = add(new ColorSetting("Background Color", 0x8A0E0E14));
	public final BooleanSetting rounded = add(new BooleanSetting("Rounded Corners", true));
	public final BooleanSetting border = add(new BooleanSetting("Accent Border", false));
	public final ColorSetting textColor = add(new ColorSetting("Text Color", 0xFFFFFFFF));
	public final ColorSetting accentColor = add(new ColorSetting("Accent Color", 0xFFFF4F9A));
	public final BooleanSetting textShadow = add(new BooleanSetting("Text Shadow", true));

	private final double defaultX;
	private final double defaultY;
	private double x;
	private double y;

	/** Layout size in unscaled units, set by {@link #layout(boolean)}. */
	protected int width = 40;
	protected int height = 14;

	/** Last on-screen bounds (scaled), used by the HUD editor. */
	public int screenX, screenY, screenW, screenH;

	protected HudModule(String name, String description, boolean enabledByDefault, double defaultX, double defaultY) {
		super(name, description, Category.HUD, enabledByDefault);
		this.defaultX = defaultX;
		this.defaultY = defaultY;
		this.x = defaultX;
		this.y = defaultY;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setPosition(double x, double y) {
		this.x = Math.max(0, Math.min(1, x));
		this.y = Math.max(0, Math.min(1, y));
	}

	public void resetPosition() {
		this.x = defaultX;
		this.y = defaultY;
		this.scale.reset();
	}

	/** Compute {@link #width} / {@link #height}. */
	protected abstract void layout(boolean preview);

	/** Draw at local origin (0,0); the base class handles translation and scale. */
	protected abstract void draw(GuiGraphics g, Font font, boolean preview);

	/** Return false to skip drawing this frame (e.g. no target). Preview always draws. */
	protected boolean shouldRender() {
		return true;
	}

	public final void render(GuiGraphics g, boolean preview) {
		if (!preview && !shouldRender()) {
			screenW = screenH = 0;
			return;
		}
		Font font = mc().font;
		layout(preview);
		float sc = scale.getFloat();
		int sw = g.guiWidth();
		int sh = g.guiHeight();
		int w = Math.max(1, Math.round(width * sc));
		int h = Math.max(1, Math.round(height * sc));
		int px = (int) Math.round(x * sw);
		int py = (int) Math.round(y * sh);
		px = Math.max(0, Math.min(sw - w, px));
		py = Math.max(0, Math.min(sh - h, py));
		screenX = px;
		screenY = py;
		screenW = w;
		screenH = h;

		g.pose().pushMatrix();
		g.pose().translate(px, py);
		g.pose().scale(sc, sc);
		draw(g, font, preview);
		g.pose().popMatrix();
	}

	// ------------------------------------------------------------- helpers

	protected void drawBackground(GuiGraphics g, int x, int y, int w, int h) {
		if (!background.isOn()) return;
		int c = backgroundColor.getColor();
		if (rounded.isOn()) Render2D.round(g, x, y, w, h, 4, c);
		else Render2D.rect(g, x, y, w, h, c);
		if (border.isOn()) {
			int a = accentColor.getColor();
			if (rounded.isOn()) Render2D.round(g, x + 2, y + h - 1, w - 4, 1, 0, a);
			else Render2D.rect(g, x, y + h - 1, w, 1, a);
		}
	}

	protected boolean shadow() {
		return textShadow.isOn();
	}
}
