package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.packs.repository.Pack;

import java.util.ArrayList;
import java.util.List;

public class PackDisplayHud extends HudModule {
	private final List<String> packs = new ArrayList<>();

	public PackDisplayHud() {
		super("Pack Display", "Shows which resource packs are active.", false, 0.005, 0.86);
	}

	@Override
	protected void layout(boolean preview) {
		packs.clear();
		for (Pack pack : mc().getResourcePackRepository().getSelectedPacks()) {
			String id = pack.getId();
			if (id.equals("vanilla") || id.equals("fabric") || id.startsWith("fabric") || id.equals("mod_resources")) continue;
			packs.add(pack.getTitle().getString());
		}
		if (packs.isEmpty()) packs.add("Default");
		Font font = mc().font;
		int w = font.width("Resource Packs");
		for (String s : packs) w = Math.max(w, font.width(s));
		width = w + 10;
		height = 14 + packs.size() * 10 + 2;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		g.drawString(font, "Resource Packs", 5, 4, accentColor.getColor(), shadow());
		int y = 15;
		for (int i = packs.size() - 1; i >= 0; i--) {
			g.drawString(font, packs.get(i), 5, y, textColor.getColor(), shadow());
			y += 10;
		}
	}
}
