package com.maddy.client.module;

import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ModeSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Single line "Label: value" HUD element (FPS, Ping, CPS, ...). */
public abstract class TextHudModule extends HudModule {
	public final BooleanSetting showLabel = add(new BooleanSetting("Show Label", true));
	public final ModeSetting style = add(new ModeSetting("Style", "Label: Value", "Label: Value", "Value Label", "[Label: Value]"));

	private String cached = "";
	private int labelLen;

	protected TextHudModule(String name, String description, boolean enabledByDefault, double x, double y) {
		super(name, description, enabledByDefault, x, y);
	}

	protected abstract String label();

	protected abstract String value(boolean preview);

	@Override
	protected void layout(boolean preview) {
		String v = value(preview);
		String l = label();
		if (!showLabel.isOn() || l.isEmpty()) {
			cached = style.is("[Label: Value]") ? "[" + v + "]" : v;
			labelLen = 0;
		} else if (style.is("Value Label")) {
			cached = v + " " + l;
			labelLen = -l.length();
		} else if (style.is("[Label: Value]")) {
			cached = "[" + l + ": " + v + "]";
			labelLen = l.length() + 2;
		} else {
			cached = l + ": " + v;
			labelLen = l.length() + 1;
		}
		width = mc().font.width(cached) + 10;
		height = 16;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		int ty = (height - 8) / 2 + 1;
		if (labelLen > 0) {
			String a = cached.substring(0, labelLen);
			String b = cached.substring(labelLen);
			g.drawString(font, a, 5, ty, accentColor.getColor(), shadow());
			g.drawString(font, b, 5 + font.width(a), ty, textColor.getColor(), shadow());
		} else if (labelLen < 0) {
			int split = cached.length() + labelLen;
			String a = cached.substring(0, split);
			String b = cached.substring(split);
			g.drawString(font, a, 5, ty, textColor.getColor(), shadow());
			g.drawString(font, b, 5 + font.width(a), ty, accentColor.getColor(), shadow());
		} else {
			g.drawString(font, cached, 5, ty, textColor.getColor(), shadow());
		}
	}
}
