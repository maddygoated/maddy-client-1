package com.maddy.client.module;

public enum Category {
	HUD("HUD", "H"),
	RENDER("Render", "R"),
	PLAYER("Player", "P"),
	UTILITY("Utility", "U");

	private final String display;
	private final String glyph;

	Category(String display, String glyph) {
		this.display = display;
		this.glyph = glyph;
	}

	public String display() {
		return display;
	}

	public String glyph() {
		return glyph;
	}
}
