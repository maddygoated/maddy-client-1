package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ColorSetting;
import com.maddy.client.render.Render2D;
import com.maddy.client.tracker.CpsTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class KeystrokesHud extends HudModule {
	private final BooleanSetting showMouse = add(new BooleanSetting("Show Mouse Buttons", true));
	private final BooleanSetting showCps = add(new BooleanSetting("Show CPS", true));
	private final BooleanSetting showSpace = add(new BooleanSetting("Show Space Bar", true));
	private final ColorSetting pressedColor = add(new ColorSetting("Pressed Color", 0xD9FF4F9A));

	private static final int KEY = 22;
	private static final int GAP = 2;
	private final float[] anim = new float[7];

	public KeystrokesHud() {
		super("Keystrokes", "Shows WASD, mouse and space presses with CPS.", true, 0.005, 0.70);
	}

	@Override
	protected void layout(boolean preview) {
		width = KEY * 3 + GAP * 2;
		height = KEY * 2 + GAP;
		if (showMouse.isOn()) height += KEY + GAP;
		if (showSpace.isOn()) height += 11 + GAP;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		Options o = mc().options;
		int y = 0;
		key(g, font, 0, "W", o.keyUp, KEY + GAP, y, KEY, KEY);
		y += KEY + GAP;
		key(g, font, 1, "A", o.keyLeft, 0, y, KEY, KEY);
		key(g, font, 2, "S", o.keyDown, KEY + GAP, y, KEY, KEY);
		key(g, font, 3, "D", o.keyRight, (KEY + GAP) * 2, y, KEY, KEY);
		y += KEY + GAP;
		if (showMouse.isOn()) {
			int mw = (width - GAP) / 2;
			String l = showCps.isOn() ? "LMB" : "LMB";
			key(g, font, 4, l, o.keyAttack, 0, y, mw, KEY);
			key(g, font, 5, "RMB", o.keyUse, mw + GAP, y, width - mw - GAP, KEY);
			if (showCps.isOn()) {
				Render2D.scaledText(g, font, CpsTracker.left() + " CPS", 0 + mw / 2f - font.width(CpsTracker.left() + " CPS") * 0.3f, y + 14, 0.6f, Render2D.alpha(textColor.getColor(), 0.85f), false);
				Render2D.scaledText(g, font, CpsTracker.right() + " CPS", mw + GAP + (width - mw - GAP) / 2f - font.width(CpsTracker.right() + " CPS") * 0.3f, y + 14, 0.6f, Render2D.alpha(textColor.getColor(), 0.85f), false);
			}
			y += KEY + GAP;
		}
		if (showSpace.isOn()) {
			boolean down = o.keyJump.isDown();
			anim[6] = Render2D.approach(anim[6], down ? 1 : 0, 0.35f);
			int bg = Render2D.lerpColor(backgroundColor.getColor(), pressedColor.getColor(), anim[6]);
			box(g, 0, y, width, 11, bg);
			Render2D.rect(g, width / 2 - 12, y + 5, 24, 1, Render2D.lerpColor(textColor.getColor(), 0xFFFFFFFF, anim[6]));
		}
	}

	private void key(GuiGraphics g, Font font, int idx, String label, KeyMapping mapping, int x, int y, int w, int h) {
		boolean down = mapping.isDown();
		anim[idx] = Render2D.approach(anim[idx], down ? 1 : 0, 0.35f);
		int bg = Render2D.lerpColor(backgroundColor.getColor(), pressedColor.getColor(), anim[idx]);
		box(g, x, y, w, h, bg);
		int ty = (idx == 4 || idx == 5) && showCps.isOn() ? y + 5 : y + (h - 8) / 2 + 1;
		Render2D.centered(g, font, label, x + w / 2, ty, Render2D.lerpColor(textColor.getColor(), 0xFFFFFFFF, anim[idx]), shadow());
	}

	private void box(GuiGraphics g, int x, int y, int w, int h, int color) {
		if (!background.isOn() && color == backgroundColor.getColor()) return;
		if (rounded.isOn()) Render2D.round(g, x, y, w, h, 4, color);
		else Render2D.rect(g, x, y, w, h, color);
	}
}
