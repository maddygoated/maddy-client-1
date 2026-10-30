package com.maddy.client.render;

/** The Maddy's Client colour palette (pink to violet). */
public final class Theme {
	public static final int ACCENT = 0xFFFF4F9A;
	public static final int ACCENT_2 = 0xFF8B5CF6;
	public static final int BACKDROP = 0x99060610;
	public static final int PANEL = 0xF2111118;
	public static final int SIDEBAR = 0xF20B0B11;
	public static final int CARD = 0xFF1B1B25;
	public static final int CARD_HOVER = 0xFF242432;
	public static final int CARD_ON = 0xFF2A1C2E;
	public static final int STROKE = 0x22FFFFFF;
	public static final int TEXT = 0xFFFFFFFF;
	public static final int MUTED = 0xFF9A9AB2;
	public static final int DIM = 0xFF5D5D73;
	public static final int TOGGLE_OFF = 0xFF3A3A4A;

	private Theme() {
	}

	public static int accentAt(float t) {
		return Render2D.lerpColor(ACCENT, ACCENT_2, t);
	}
}
