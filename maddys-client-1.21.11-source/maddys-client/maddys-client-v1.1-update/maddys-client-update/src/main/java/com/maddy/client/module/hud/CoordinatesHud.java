package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ModeSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesHud extends HudModule {
	private final ModeSetting layoutMode = add(new ModeSetting("Layout", "Vertical", "Vertical", "Horizontal"));
	private final BooleanSetting showFacing = add(new BooleanSetting("Show Facing", true));
	private final BooleanSetting showNether = add(new BooleanSetting("Show Nether Coords", false));
	private final BooleanSetting decimals = add(new BooleanSetting("Decimals", false));

	private final List<String[]> rows = new ArrayList<>();

	public CoordinatesHud() {
		super("Coordinates", "Shows your X / Y / Z position and facing.", true, 0.005, 0.21);
	}

	private String fmt(double v) {
		return decimals.isOn() ? String.format("%.1f", v) : String.valueOf((int) Math.floor(v));
	}

	@Override
	protected void layout(boolean preview) {
		rows.clear();
		var p = mc().player;
		double x = p == null ? 128 : p.getX();
		double y = p == null ? 64 : p.getY();
		double z = p == null ? -256 : p.getZ();
		if (layoutMode.is("Horizontal")) {
			rows.add(new String[]{"XYZ ", fmt(x) + ", " + fmt(y) + ", " + fmt(z)});
		} else {
			rows.add(new String[]{"X ", fmt(x)});
			rows.add(new String[]{"Y ", fmt(y)});
			rows.add(new String[]{"Z ", fmt(z)});
		}
		if (showFacing.isOn()) {
			String dir = p == null ? "north" : p.getDirection().getName();
			rows.add(new String[]{"F ", Character.toUpperCase(dir.charAt(0)) + dir.substring(1) + axis(dir)});
		}
		if (showNether.isOn()) {
			boolean inNether = p != null && mc().level != null && mc().level.dimension().equals(net.minecraft.world.level.Level.NETHER);
			double f = inNether ? 8 : 0.125;
			rows.add(new String[]{inNether ? "OW " : "N ", fmt(x * f) + ", " + fmt(z * f)});
		}
		Font font = mc().font;
		int w = 0;
		for (String[] r : rows) w = Math.max(w, font.width(r[0] + r[1]));
		width = w + 10;
		height = rows.size() * 10 + 6;
	}

	private static String axis(String dir) {
		return switch (dir) {
			case "north" -> " (-Z)";
			case "south" -> " (+Z)";
			case "east" -> " (+X)";
			case "west" -> " (-X)";
			default -> "";
		};
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		int y = 4;
		for (String[] r : rows) {
			g.drawString(font, r[0], 5, y, accentColor.getColor(), shadow());
			g.drawString(font, r[1], 5 + font.width(r[0]), y, textColor.getColor(), shadow());
			y += 10;
		}
	}
}
