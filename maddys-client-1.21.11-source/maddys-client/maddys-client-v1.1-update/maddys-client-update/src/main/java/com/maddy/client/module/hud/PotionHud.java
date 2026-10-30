package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class PotionHud extends HudModule {
	public final BooleanSetting hideVanilla = add(new BooleanSetting("Hide Vanilla Effects", true));
	private final BooleanSetting showLevel = add(new BooleanSetting("Show Level", true));
	private final BooleanSetting colorBar = add(new BooleanSetting("Effect Colour Bar", true));

	private final List<String[]> rows = new ArrayList<>();
	private final List<Integer> colors = new ArrayList<>();

	public PotionHud() {
		super("Potion Effects", "Lists your active effects with time left.", true, 0.88, 0.08);
	}

	@Override
	protected boolean shouldRender() {
		return mc().player != null && !mc().player.getActiveEffects().isEmpty();
	}

	@Override
	protected void layout(boolean preview) {
		rows.clear();
		colors.clear();
		var p = mc().player;
		if (p != null && !p.getActiveEffects().isEmpty()) {
			for (MobEffectInstance e : p.getActiveEffects()) {
				String name = e.getEffect().value().getDisplayName().getString();
				if (showLevel.isOn() && e.getAmplifier() > 0) name += " " + roman(e.getAmplifier() + 1);
				rows.add(new String[]{name, e.isInfiniteDuration() ? "**:**" : time(e.getDuration())});
				colors.add(0xFF000000 | e.getEffect().value().getColor());
			}
		} else {
			rows.add(new String[]{"Speed II", "1:30"});
			colors.add(0xFF7CAFC6);
			rows.add(new String[]{"Strength", "3:00"});
			colors.add(0xFFFFC700);
		}
		Font font = mc().font;
		int w = 0;
		for (String[] r : rows) w = Math.max(w, font.width(r[0]) + font.width(r[1]) + 10);
		width = w + 14;
		height = rows.size() * 12 + 4;
	}

	private static String time(int ticks) {
		int s = ticks / 20;
		return String.format("%d:%02d", s / 60, s % 60);
	}

	private static String roman(int n) {
		return switch (n) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			case 6 -> "VI";
			case 7 -> "VII";
			case 8 -> "VIII";
			case 9 -> "IX";
			case 10 -> "X";
			default -> String.valueOf(n);
		};
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		int y = 3;
		for (int i = 0; i < rows.size(); i++) {
			String[] r = rows.get(i);
			if (colorBar.isOn()) Render2D.round(g, 4, y + 1, 2, 9, 1, colors.get(i));
			g.drawString(font, r[0], 10, y + 2, textColor.getColor(), shadow());
			g.drawString(font, r[1], width - 5 - font.width(r[1]), y + 2, accentColor.getColor(), shadow());
			y += 12;
		}
	}
}
