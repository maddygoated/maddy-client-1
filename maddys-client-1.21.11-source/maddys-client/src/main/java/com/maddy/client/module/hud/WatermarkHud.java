package com.maddy.client.module.hud;

import com.maddy.client.Branding;
import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.render.Render2D;
import com.maddy.client.render.Theme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class WatermarkHud extends HudModule {
	private final BooleanSetting showVersion = add(new BooleanSetting("Show Version", true));
	private final BooleanSetting showFps = add(new BooleanSetting("Show FPS", false));
	private String suffix = "";

	public WatermarkHud() {
		super("Watermark", "Shows the Maddy's Client logo on screen.", true, 0.005, 0.01);
	}

	@Override
	protected void layout(boolean preview) {
		StringBuilder sb = new StringBuilder();
		if (showVersion.isOn()) sb.append(" ").append(Branding.MC_VERSION);
		if (showFps.isOn()) sb.append(" | ").append(mc().getFps()).append(" FPS");
		suffix = sb.toString();
		Font f = mc().font;
		width = 16 + f.width(Branding.NAME_UPPER) + f.width(suffix) + 6;
		height = 16;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		Render2D.roundGradientH(g, 4, 4, 8, 8, 2, Theme.ACCENT, Theme.ACCENT_2);
		int x = 16;
		Render2D.gradientText(g, font, Branding.NAME_UPPER, x, 5, Theme.ACCENT, Theme.ACCENT_2, shadow());
		x += font.width(Branding.NAME_UPPER);
		if (!suffix.isEmpty()) g.drawString(font, suffix, x, 5, Render2D.alpha(textColor.getColor(), 0.75f), shadow());
	}
}
