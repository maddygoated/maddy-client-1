package com.maddy.client.module.hud;

import com.maddy.client.MaddysClient;
import com.maddy.client.module.Category;
import com.maddy.client.module.HudModule;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Lists your active (non-HUD) mods, sorted by length, with a chroma side bar. */
public class ArrayListHud extends HudModule {
	private final BooleanSetting includeHud = add(new BooleanSetting("Include HUD Mods", false));
	private final BooleanSetting chromaBar = add(new BooleanSetting("Chroma Bar", true));
	private final List<String> names = new ArrayList<>();

	public ArrayListHud() {
		super("Mod List", "Shows all enabled mods in the corner of the screen.", false, 0.90, 0.01);
	}

	@Override
	protected void layout(boolean preview) {
		names.clear();
		for (Module m : MaddysClient.modules().all()) {
			if (!m.isEnabled() || m == this || m instanceof com.maddy.client.module.utility.ClientSettings) continue;
			if (m.getCategory() == Category.HUD && !includeHud.isOn()) continue;
			names.add(m.getName());
		}
		if (names.isEmpty()) names.add(preview ? "Toggle Sprint" : "");
		Font font = mc().font;
		names.sort(Comparator.comparingInt((String s) -> font.width(s)).reversed());
		int w = 0;
		for (String s : names) w = Math.max(w, font.width(s));
		width = w + 10;
		height = names.size() * 11 + 1;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		int y = 0;
		for (int i = 0; i < names.size(); i++) {
			String s = names.get(i);
			if (s.isEmpty()) continue;
			int w = font.width(s) + 8;
			int x = width - w;
			if (background.isOn()) Render2D.rect(g, x, y, w, 11, backgroundColor.getColor());
			int c = chromaBar.isOn() ? Render2D.chroma(i * 0.06f) : accentColor.getColor();
			Render2D.rect(g, width - 2, y, 2, 11, c);
			g.drawString(font, s, x + 3, y + 2, chromaBar.isOn() ? c : textColor.getColor(), shadow());
			y += 11;
		}
	}
}
