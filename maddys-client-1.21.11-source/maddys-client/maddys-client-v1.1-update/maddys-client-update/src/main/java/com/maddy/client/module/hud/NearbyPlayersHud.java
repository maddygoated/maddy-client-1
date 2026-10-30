package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.NumberSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Lists players near you with their distance and health. */
public class NearbyPlayersHud extends HudModule {
	private final NumberSetting maxShown = add(new NumberSetting("Max Players", 5, 1, 10, 1));
	private final List<String[]> rows = new ArrayList<>();
	private final List<Integer> colors = new ArrayList<>();

	public NearbyPlayersHud() {
		super("Nearby Players", "Players around you with distance and health.", false, 0.80, 0.30);
	}

	@Override
	protected void layout(boolean preview) {
		rows.clear();
		colors.clear();
		var me = mc().player;
		var level = mc().level;
		if (me != null && level != null) {
			List<Player> others = new ArrayList<>();
			for (Player pl : level.players()) if (pl != me) others.add(pl);
			others.sort(Comparator.comparingDouble(pl -> pl.distanceTo(me)));
			for (int i = 0; i < Math.min(others.size(), maxShown.getInt()); i++) {
				Player pl = others.get(i);
				rows.add(new String[]{pl.getName().getString(), String.format("%.0fm  %.0f\u2764", pl.distanceTo(me), pl.getHealth())});
				colors.add(Render2D.healthColor(pl.getHealth() / Math.max(1, pl.getMaxHealth())));
			}
		}
		if (rows.isEmpty()) {
			rows.add(new String[]{preview ? "Steve" : "Nobody nearby", preview ? "12m  20\u2764" : ""});
			colors.add(Theme_muted());
		}
		Font font = mc().font;
		int w = font.width("Nearby Players");
		for (String[] r : rows) w = Math.max(w, font.width(r[0]) + font.width(r[1]) + 12);
		width = w + 10;
		height = 14 + rows.size() * 10 + 3;
	}

	private static int Theme_muted() {
		return 0xFF9A9AB2;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		g.drawString(font, "Nearby Players", 5, 4, accentColor.getColor(), shadow());
		int y = 15;
		for (int i = 0; i < rows.size(); i++) {
			g.drawString(font, rows.get(i)[0], 5, y, textColor.getColor(), shadow());
			g.drawString(font, rows.get(i)[1], width - 5 - font.width(rows.get(i)[1]), y, colors.get(i), shadow());
			y += 10;
		}
	}
}
