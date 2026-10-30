package com.maddy.client.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Small 2D drawing toolkit used by the HUD and the menus. */
public final class Render2D {
	private Render2D() {
	}

	public static void rect(GuiGraphics g, int x, int y, int w, int h, int color) {
		if (w <= 0 || h <= 0) return;
		g.fill(x, y, x + w, y + h, color);
	}

	/** Filled rectangle with smooth-looking rounded corners. */
	public static void round(GuiGraphics g, int x, int y, int w, int h, int radius, int color) {
		if (w <= 0 || h <= 0) return;
		int r = Math.min(radius, Math.min(w, h) / 2);
		if (r <= 0) {
			rect(g, x, y, w, h, color);
			return;
		}
		g.fill(x, y + r, x + w, y + h - r, color);
		for (int i = 0; i < r; i++) {
			double dy = r - i - 0.5;
			int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
			g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
			g.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
		}
	}

	/** Rounded rectangle with a horizontal two-colour gradient (drawn as vertical slices). */
	public static void roundGradientH(GuiGraphics g, int x, int y, int w, int h, int radius, int left, int right) {
		if (w <= 0 || h <= 0) return;
		int r = Math.min(radius, Math.min(w, h) / 2);
		for (int i = 0; i < w; i++) {
			int c = lerpColor(left, right, w == 1 ? 0 : (float) i / (w - 1));
			int inset = 0;
			int dxEdge = Math.min(i, w - 1 - i);
			if (dxEdge < r) {
				double dx = r - dxEdge - 0.5;
				inset = (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dx * dx)));
			}
			g.fill(x + i, y + inset, x + i + 1, y + h - inset, c);
		}
	}

	public static void outline(GuiGraphics g, int x, int y, int w, int h, int color) {
		g.fill(x, y, x + w, y + 1, color);
		g.fill(x, y + h - 1, x + w, y + h, color);
		g.fill(x, y + 1, x + 1, y + h - 1, color);
		g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
	}

	public static void circle(GuiGraphics g, int cx, int cy, int radius, int color) {
		round(g, cx - radius, cy - radius, radius * 2, radius * 2, radius, color);
	}

	/** Ring outline (approximated with 1px horizontal runs). */
	public static void ring(GuiGraphics g, int cx, int cy, int radius, int thickness, int color) {
		int rOut = radius;
		int rIn = Math.max(0, radius - thickness);
		for (int dy = -rOut; dy < rOut; dy++) {
			double yy = dy + 0.5;
			double xo = Math.sqrt(Math.max(0, rOut * rOut - yy * yy));
			double xi = Math.abs(yy) < rIn ? Math.sqrt(Math.max(0, rIn * rIn - yy * yy)) : 0;
			int o = (int) Math.round(xo);
			int in = (int) Math.round(xi);
			if (in <= 0) {
				g.fill(cx - o, cy + dy, cx + o, cy + dy + 1, color);
			} else {
				g.fill(cx - o, cy + dy, cx - in, cy + dy + 1, color);
				g.fill(cx + in, cy + dy, cx + o, cy + dy + 1, color);
			}
		}
	}

	public static void text(GuiGraphics g, Font font, String s, int x, int y, int color, boolean shadow) {
		g.drawString(font, s, x, y, color, shadow);
	}

	public static void centered(GuiGraphics g, Font font, String s, int cx, int y, int color, boolean shadow) {
		g.drawString(font, s, cx - font.width(s) / 2, y, color, shadow);
	}

	/** Draws text where every character fades along a two-colour gradient. */
	public static void gradientText(GuiGraphics g, Font font, String s, int x, int y, int from, int to, boolean shadow) {
		int cx = x;
		int n = Math.max(1, s.length() - 1);
		for (int i = 0; i < s.length(); i++) {
			String ch = String.valueOf(s.charAt(i));
			g.drawString(font, ch, cx, y, lerpColor(from, to, (float) i / n), shadow);
			cx += font.width(ch);
		}
	}

	/** Scaled text helper. */
	public static void scaledText(GuiGraphics g, Font font, String s, float x, float y, float scale, int color, boolean shadow) {
		g.pose().pushMatrix();
		g.pose().translate(x, y);
		g.pose().scale(scale, scale);
		g.drawString(font, s, 0, 0, color, shadow);
		g.pose().popMatrix();
	}

	// ---------------------------------------------------------------- colours

	public static int alpha(int color, float alpha) {
		int a = (int) (((color >>> 24) & 0xFF) * clamp01(alpha));
		return (a << 24) | (color & 0x00FFFFFF);
	}

	public static int withAlpha(int color, int alpha) {
		return ((alpha & 0xFF) << 24) | (color & 0x00FFFFFF);
	}

	public static int lerpColor(int a, int b, float t) {
		t = clamp01(t);
		int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
		int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
		return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16)
				| ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
	}

	public static int hsb(float hue, float sat, float bri) {
		float h = (hue - (float) Math.floor(hue)) * 6f;
		int i = (int) h;
		float f = h - i;
		float p = bri * (1 - sat), q = bri * (1 - sat * f), t = bri * (1 - sat * (1 - f));
		float r, gg, b;
		switch (i) {
			case 0 -> { r = bri; gg = t; b = p; }
			case 1 -> { r = q; gg = bri; b = p; }
			case 2 -> { r = p; gg = bri; b = t; }
			case 3 -> { r = p; gg = q; b = bri; }
			case 4 -> { r = t; gg = p; b = bri; }
			default -> { r = bri; gg = p; b = q; }
		}
		return 0xFF000000 | ((int) (r * 255) << 16) | ((int) (gg * 255) << 8) | (int) (b * 255);
	}

	/** Animated rainbow colour; offset shifts the phase (e.g. per line). */
	public static int chroma(float offset) {
		float hue = ((System.currentTimeMillis() % 4000L) / 4000f) + offset;
		return hsb(hue, 0.55f, 1f);
	}

	public static int healthColor(float pct) {
		if (pct > 0.6f) return 0xFF4ADE80;
		if (pct > 0.3f) return 0xFFFACC15;
		return 0xFFF87171;
	}

	public static float clamp01(float v) {
		return v < 0 ? 0 : (v > 1 ? 1 : v);
	}

	/** Frame-rate independent smoothing toward a target. */
	public static float approach(float current, float target, float speed) {
		float diff = target - current;
		if (Math.abs(diff) < 0.001f) return target;
		return current + diff * Math.min(1f, speed);
	}

	public static boolean inside(double mx, double my, double x, double y, double w, double h) {
		return mx >= x && my >= y && mx < x + w && my < y + h;
	}
}
