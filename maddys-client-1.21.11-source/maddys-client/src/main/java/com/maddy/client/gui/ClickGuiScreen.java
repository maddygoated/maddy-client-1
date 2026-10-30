package com.maddy.client.gui;

import com.maddy.client.Branding;
import com.maddy.client.MaddysClient;
import com.maddy.client.module.Category;
import com.maddy.client.module.HudModule;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ColorSetting;
import com.maddy.client.module.setting.ModeSetting;
import com.maddy.client.module.setting.NumberSetting;
import com.maddy.client.module.setting.Setting;
import com.maddy.client.render.Render2D;
import com.maddy.client.render.Theme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/** The Maddy's Client mod menu (Right Shift). */
public class ClickGuiScreen extends Screen {
	private static final int W = 460;
	private static final int H = 280;
	private static final int SIDEBAR = 118;
	private static final int CARD_H = 50;
	private static final int GAP = 8;
	private static final int ROW_H = 26;

	private static Category lastCategory = null;

	private final Screen parent;
	private Category category = lastCategory;
	private Module openModule;
	private float scroll;
	private float targetScroll;
	private float openAnim;
	private final float[] categoryAnim = new float[Category.values().length + 1];

	private NumberSetting draggingSlider;
	private int sliderX;
	private int sliderW;

	private float s = 1f;
	private int px;
	private int py;

	public ClickGuiScreen(Screen parent) {
		super(Component.literal(Branding.NAME));
		this.parent = parent;
	}

	@Override
	protected void init() {
		s = Math.min(width / (float) (W + 30), height / (float) (H + 30));
		s = Math.max(0.5f, Math.min(1.5f, s));
		px = (int) ((width / s - W) / 2);
		py = (int) ((height / s - H) / 2);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
		// drawn manually in render()
	}

	// ================================================================ render

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		openAnim = Render2D.approach(openAnim, 1f, 0.18f);
		scroll = Render2D.approach(scroll, targetScroll, 0.3f);

		g.fill(0, 0, width, height, Render2D.alpha(Theme.BACKDROP, openAnim));

		double mx = mouseX / s;
		double my = mouseY / s;
		Font font = this.font;

		g.pose().pushMatrix();
		g.pose().scale(s, s);
		float pop = 0.94f + 0.06f * openAnim;
		g.pose().translate(px + W / 2f, py + H / 2f);
		g.pose().scale(pop, pop);
		g.pose().translate(-(px + W / 2f), -(py + H / 2f));

		// shadow + panel
		Render2D.round(g, px - 3, py - 3, W + 6, H + 6, 12, 0x40000000);
		Render2D.round(g, px, py, W, H, 10, Theme.PANEL);
		Render2D.round(g, px, py, SIDEBAR, H, 10, Theme.SIDEBAR);
		Render2D.rect(g, px + SIDEBAR - 10, py, 10, H, Theme.SIDEBAR);
		Render2D.rect(g, px + SIDEBAR, py + 10, 1, H - 20, Theme.STROKE);

		drawSidebar(g, font, mx, my);
		if (openModule == null) drawModules(g, font, mx, my);
		else drawSettings(g, font, mx, my);

		g.pose().popMatrix();
	}

	private void drawSidebar(GuiGraphics g, Font font, double mx, double my) {
		// logo
		Render2D.roundGradientH(g, px + 12, py + 12, 24, 24, 6, Theme.ACCENT, Theme.ACCENT_2);
		Render2D.scaledText(g, font, "M", px + 18.5f, py + 16.5f, 1.6f, 0xFFFFFFFF, true);
		Render2D.gradientText(g, font, "MADDY'S", px + 42, py + 14, Theme.ACCENT, Theme.ACCENT_2, true);
		Render2D.scaledText(g, font, "CLIENT  " + Branding.MC_VERSION, px + 42, py + 25, 0.7f, Theme.MUTED, false);

		int y = py + 52;
		Render2D.scaledText(g, font, "CATEGORIES", px + 14, y, 0.6f, Theme.DIM, false);
		y += 10;
		for (int i = 0; i <= Category.values().length; i++) {
			Category c = i == 0 ? null : Category.values()[i - 1];
			String label = c == null ? "All Mods" : c.display();
			String glyph = c == null ? "*" : c.glyph();
			boolean selected = category == c && openModule == null || openModule != null && c == openModule.getCategory() && category == c;
			boolean hover = Render2D.inside(mx, my, px + 8, y, SIDEBAR - 16, 22);
			categoryAnim[i] = Render2D.approach(categoryAnim[i], selected ? 1f : (hover ? 0.4f : 0f), 0.25f);
			float a = categoryAnim[i];
			if (a > 0.01f) {
				Render2D.round(g, px + 8, y, SIDEBAR - 16, 22, 6, Render2D.withAlpha(Theme.ACCENT, (int) (0x30 * a)));
			}
			if (selected) Render2D.round(g, px + 8, y + 6, 3, 10, 1, Theme.ACCENT);
			Render2D.round(g, px + 16, y + 4, 14, 14, 4, selected ? Theme.ACCENT : 0xFF262634);
			Render2D.centered(g, font, glyph, px + 23, y + 7, 0xFFFFFFFF, false);
			g.drawString(font, label, px + 36, y + 7, selected ? Theme.TEXT : Render2D.lerpColor(Theme.MUTED, Theme.TEXT, a), false);
			int count = 0;
			for (Module m : MaddysClient.modules().byCategory(c)) if (m.isEnabled()) count++;
			String cnt = String.valueOf(count);
			Render2D.scaledText(g, font, cnt, px + SIDEBAR - 16 - font.width(cnt) * 0.7f, y + 8, 0.7f, Theme.DIM, false);
			y += 25;
		}

		// HUD editor button
		boolean canEdit = minecraft != null && minecraft.level != null;
		int bx = px + 10, by = py + H - 54, bw = SIDEBAR - 20, bh = 20;
		boolean hover = canEdit && Render2D.inside(mx, my, bx, by, bw, bh);
		if (canEdit) Render2D.roundGradientH(g, bx, by, bw, bh, 6, hover ? 0xFFFF6BAE : Theme.ACCENT, hover ? 0xFFA07BFF : Theme.ACCENT_2);
		else Render2D.round(g, bx, by, bw, bh, 6, 0xFF262634);
		Render2D.centered(g, font, "Edit HUD Layout", bx + bw / 2, by + 6, canEdit ? 0xFFFFFFFF : Theme.DIM, canEdit);

		Render2D.scaledText(g, font, "v" + Branding.VERSION + "  \u2022  Right Shift", px + 12, py + H - 24, 0.6f, Theme.DIM, false);
		Render2D.scaledText(g, font, "Made for Maddy", px + 12, py + H - 16, 0.6f, Theme.DIM, false);
	}

	private List<Module> visibleModules() {
		return new ArrayList<>(MaddysClient.modules().byCategory(category));
	}

	private int contentX() {
		return px + SIDEBAR + 12;
	}

	private int contentY() {
		return py + 40;
	}

	private int contentW() {
		return W - SIDEBAR - 24;
	}

	private int contentH() {
		return H - 40 - 10;
	}

	private void drawHeader(GuiGraphics g, Font font, String title, String subtitle, boolean back, double mx, double my) {
		int x = contentX();
		if (back) {
			boolean hover = Render2D.inside(mx, my, x, py + 12, 18, 18);
			Render2D.round(g, x, py + 12, 18, 18, 5, hover ? Theme.CARD_HOVER : Theme.CARD);
			Render2D.centered(g, font, "<", x + 9, py + 17, 0xFFFFFFFF, false);
			x += 24;
		}
		Render2D.scaledText(g, font, title, x, py + 12, 1.25f, Theme.TEXT, true);
		Render2D.scaledText(g, font, subtitle, x, py + 25, 0.7f, Theme.MUTED, false);
	}

	private void drawModules(GuiGraphics g, Font font, double mx, double my) {
		List<Module> list = visibleModules();
		int enabled = 0;
		for (Module m : list) if (m.isEnabled()) enabled++;
		drawHeader(g, font, category == null ? "All Mods" : category.display(), list.size() + " mods  \u2022  " + enabled + " enabled  \u2022  right-click for settings", false, mx, my);

		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		int cardW = (cw - GAP) / 2;
		int rows = (list.size() + 1) / 2;
		int total = rows * (CARD_H + GAP) - GAP;
		clampScroll(total, ch);

		g.enableScissor(cx - 2, cy, cx + cw + 2, cy + ch);
		for (int i = 0; i < list.size(); i++) {
			Module m = list.get(i);
			int x = cx + (i % 2) * (cardW + GAP);
			int y = cy + (i / 2) * (CARD_H + GAP) - Math.round(scroll);
			if (y + CARD_H < cy || y > cy + ch) continue;
			boolean hover = Render2D.inside(mx, my, x, y, cardW, CARD_H) && my >= cy && my < cy + ch;
			m.hoverAnim = Render2D.approach(m.hoverAnim, hover ? 1f : 0f, 0.3f);
			m.toggleAnim = Render2D.approach(m.toggleAnim, m.isEnabled() ? 1f : 0f, 0.25f);
			drawCard(g, font, m, x, y, cardW, mx, my);
		}
		g.disableScissor();
		drawScrollbar(g, total, ch);
	}

	private void drawCard(GuiGraphics g, Font font, Module m, int x, int y, int w, double mx, double my) {
		int bg = Render2D.lerpColor(Theme.CARD, Theme.CARD_HOVER, m.hoverAnim);
		bg = Render2D.lerpColor(bg, Theme.CARD_ON, m.toggleAnim * 0.6f);
		Render2D.round(g, x, y, w, CARD_H, 7, bg);
		if (m.toggleAnim > 0.01f) Render2D.round(g, x + 1, y + CARD_H - 2, w - 2, 2, 1, Render2D.alpha(Theme.ACCENT, m.toggleAnim));

		// icon tile
		if (m.toggleAnim > 0.5f) Render2D.roundGradientH(g, x + 8, y + 11, 26, 26, 6, Theme.ACCENT, Theme.ACCENT_2);
		else Render2D.round(g, x + 8, y + 11, 26, 26, 6, 0xFF2E2E3E);
		Render2D.centered(g, font, m.getGlyph(), x + 21, y + 20, 0xFFFFFFFF, true);

		// text
		g.drawString(font, trim(font, m.getName(), w - 80, 1f), x + 40, y + 10, Theme.TEXT, false);
		String desc = m.getDescription();
		float ds = 0.62f;
		int maxDesc = (int) ((w - 50) / ds);
		String[] lines = wrap(font, desc, maxDesc, 2);
		for (int i = 0; i < lines.length; i++) {
			Render2D.scaledText(g, font, lines[i], x + 40, y + 22 + i * 7, ds, Theme.MUTED, false);
		}

		// toggle
		drawSwitch(g, x + w - 30, y + 8, m.toggleAnim);

		// settings dots
		if (!m.getSettings().isEmpty()) {
			boolean dotsHover = Render2D.inside(mx, my, x + w - 24, y + CARD_H - 16, 18, 12);
			int dc = dotsHover ? Theme.ACCENT : Theme.MUTED;
			for (int i = 0; i < 3; i++) Render2D.rect(g, x + w - 20 + i * 4, y + CARD_H - 10, 2, 2, dc);
		}
	}

	private void drawSwitch(GuiGraphics g, int x, int y, float t) {
		int track = Render2D.lerpColor(Theme.TOGGLE_OFF, Theme.ACCENT, t);
		Render2D.round(g, x, y, 22, 12, 6, track);
		int kx = (int) (x + 6 + 10 * t);
		Render2D.circle(g, kx, y + 6, 4, 0xFFFFFFFF);
	}

	private void drawSettings(GuiGraphics g, Font font, double mx, double my) {
		Module m = openModule;
		drawHeader(g, font, m.getName(), m.getCategory().display() + "  \u2022  " + m.getDescription(), true, mx, my);

		// enable switch in the header
		m.toggleAnim = Render2D.approach(m.toggleAnim, m.isEnabled() ? 1f : 0f, 0.25f);
		int sx = contentX() + contentW() - 26;
		drawSwitch(g, sx, py + 15, m.toggleAnim);

		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		List<Setting<?>> settings = visibleSettings(m);
		int total = (settings.size() + 1) * (ROW_H + 4);
		clampScroll(total, ch);

		g.enableScissor(cx - 2, cy, cx + cw + 2, cy + ch);
		int y = cy - Math.round(scroll);
		for (Setting<?> setting : settings) {
			drawSettingRow(g, font, setting, cx, y, cw, mx, my);
			y += ROW_H + 4;
		}
		// reset buttons
		boolean hudMod = m instanceof HudModule;
		int bw = hudMod ? (cw - 6) / 2 : cw;
		boolean h1 = Render2D.inside(mx, my, cx, y, bw, ROW_H - 4);
		Render2D.round(g, cx, y, bw, ROW_H - 4, 6, h1 ? 0xFF3A2030 : 0xFF2A1A24);
		Render2D.centered(g, font, "Reset Settings", cx + bw / 2, y + 7, 0xFFFF8FB8, false);
		if (hudMod) {
			boolean h2 = Render2D.inside(mx, my, cx + bw + 6, y, bw, ROW_H - 4);
			Render2D.round(g, cx + bw + 6, y, bw, ROW_H - 4, 6, h2 ? Theme.CARD_HOVER : Theme.CARD);
			Render2D.centered(g, font, "Reset Position", cx + bw + 6 + bw / 2, y + 7, Theme.TEXT, false);
		}
		g.disableScissor();
		drawScrollbar(g, total, ch);
	}

	private List<Setting<?>> visibleSettings(Module m) {
		List<Setting<?>> list = new ArrayList<>();
		for (Setting<?> st : m.getSettings()) if (st.isVisible()) list.add(st);
		return list;
	}

	private void drawSettingRow(GuiGraphics g, Font font, Setting<?> setting, int x, int y, int w, double mx, double my) {
		Render2D.round(g, x, y, w, ROW_H, 6, Theme.CARD);
		g.drawString(font, setting.getName(), x + 9, y + 9, Theme.TEXT, false);
		int right = x + w - 9;

		if (setting instanceof BooleanSetting b) {
			b.anim = Render2D.approach(b.anim, b.isOn() ? 1f : 0f, 0.25f);
			drawSwitch(g, right - 22, y + 7, b.anim);
		} else if (setting instanceof NumberSetting n) {
			int bw = 110;
			int bx = right - bw;
			int by = y + 12;
			Render2D.round(g, bx, by, bw, 3, 1, 0xFF33334A);
			int fill = (int) (bw * n.percent());
			Render2D.roundGradientH(g, bx, by, Math.max(2, fill), 3, 1, Theme.ACCENT, Theme.ACCENT_2);
			Render2D.circle(g, bx + fill, by + 1, 4, 0xFFFFFFFF);
			String v = n.display();
			g.drawString(font, v, bx - 8 - font.width(v), y + 9, Theme.MUTED, false);
		} else if (setting instanceof ModeSetting mode) {
			String v = mode.get();
			int pw = font.width(v) + 26;
			int bx = right - pw;
			boolean hover = Render2D.inside(mx, my, bx, y + 5, pw, 16);
			Render2D.round(g, bx, y + 5, pw, 16, 5, hover ? 0xFF34344A : 0xFF2A2A3C);
			g.drawString(font, "<", bx + 5, y + 9, Theme.ACCENT, false);
			Render2D.centered(g, font, v, bx + pw / 2, y + 9, Theme.TEXT, false);
			g.drawString(font, ">", bx + pw - 10, y + 9, Theme.ACCENT, false);
		} else if (setting instanceof ColorSetting c) {
			int sw = 10, sg = 3;
			int chromaW = 30;
			int total = ColorSetting.PALETTE.length * (sw + sg) + chromaW;
			int bx = right - total;
			for (int i = 0; i < ColorSetting.PALETTE.length; i++) {
				int col = ColorSetting.PALETTE[i];
				int sx = bx + i * (sw + sg);
				boolean selected = !c.isChroma() && (c.get() & 0xFFFFFF) == (col & 0xFFFFFF);
				if (selected) Render2D.round(g, sx - 2, y + 6, sw + 4, sw + 4, 3, 0xFFFFFFFF);
				Render2D.round(g, sx, y + 8, sw, sw, 3, col | 0xFF000000);
			}
			int cxp = bx + ColorSetting.PALETTE.length * (sw + sg);
			if (c.isChroma()) Render2D.roundGradientH(g, cxp, y + 6, chromaW - 2, 14, 4, Render2D.chroma(0f), Render2D.chroma(0.5f));
			else Render2D.round(g, cxp, y + 6, chromaW - 2, 14, 4, 0xFF2A2A3C);
			Render2D.centered(g, font, "RGB", cxp + (chromaW - 2) / 2, y + 9, 0xFFFFFFFF, c.isChroma());
			// keep alpha of default (e.g. translucent backgrounds) - shown as small preview
			Render2D.round(g, bx - 16, y + 8, 10, 10, 3, c.getColor());
		}
	}

	private void drawScrollbar(GuiGraphics g, int total, int ch) {
		if (total <= ch) return;
		int x = contentX() + contentW() + 4;
		int barH = Math.max(20, (int) ((float) ch * ch / total));
		int barY = contentY() + (int) ((ch - barH) * (scroll / (total - ch)));
		Render2D.round(g, x, barY, 3, barH, 1, 0x66FFFFFF);
	}

	private void clampScroll(int total, int ch) {
		float max = Math.max(0, total - ch);
		if (targetScroll > max) targetScroll = max;
		if (targetScroll < 0) targetScroll = 0;
	}

	// =============================================================== input

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x() / s;
		double my = event.y() / s;
		int button = event.button();

		// sidebar categories
		int y = py + 62;
		for (int i = 0; i <= Category.values().length; i++) {
			if (Render2D.inside(mx, my, px + 8, y, SIDEBAR - 16, 22)) {
				category = i == 0 ? null : Category.values()[i - 1];
				lastCategory = category;
				openModule = null;
				targetScroll = scroll = 0;
				return true;
			}
			y += 25;
		}

		// HUD editor
		if (minecraft != null && minecraft.level != null && Render2D.inside(mx, my, px + 10, py + H - 54, SIDEBAR - 20, 20)) {
			MaddysClient.saveConfig();
			minecraft.setScreen(new HudEditorScreen(this));
			return true;
		}

		if (openModule == null) {
			if (clickModules(mx, my, button)) return true;
		} else {
			if (clickSettings(mx, my, button)) return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	private boolean clickModules(double mx, double my, int button) {
		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		if (!Render2D.inside(mx, my, cx, cy, cw, ch)) return false;
		List<Module> list = visibleModules();
		int cardW = (cw - GAP) / 2;
		for (int i = 0; i < list.size(); i++) {
			Module m = list.get(i);
			int x = cx + (i % 2) * (cardW + GAP);
			int y = cy + (i / 2) * (CARD_H + GAP) - Math.round(scroll);
			if (!Render2D.inside(mx, my, x, y, cardW, CARD_H)) continue;
			boolean dots = Render2D.inside(mx, my, x + cardW - 24, y + CARD_H - 16, 18, 12);
			if ((button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || dots) && !m.getSettings().isEmpty()) {
				openModule = m;
				targetScroll = scroll = 0;
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				m.toggle();
				MaddysClient.saveConfig();
			}
			return true;
		}
		return false;
	}

	private boolean clickSettings(double mx, double my, int button) {
		Module m = openModule;
		// back
		if (Render2D.inside(mx, my, contentX(), py + 12, 18, 18)) {
			openModule = null;
			targetScroll = scroll = 0;
			return true;
		}
		// header switch
		if (Render2D.inside(mx, my, contentX() + contentW() - 26, py + 15, 22, 12)) {
			m.toggle();
			MaddysClient.saveConfig();
			return true;
		}
		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		if (!Render2D.inside(mx, my, cx, cy, cw, ch)) return false;
		int y = cy - Math.round(scroll);
		int right = cx + cw - 9;
		for (Setting<?> setting : visibleSettings(m)) {
			if (Render2D.inside(mx, my, cx, y, cw, ROW_H)) {
				if (setting instanceof BooleanSetting b) {
					b.toggle();
				} else if (setting instanceof NumberSetting n) {
					int bw = 110;
					int bx = right - bw;
					if (mx >= bx - 4) {
						draggingSlider = n;
						sliderX = bx;
						sliderW = bw;
						n.setPercent((mx - bx) / bw);
					}
				} else if (setting instanceof ModeSetting mode) {
					mode.cycle(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? -1 : 1);
				} else if (setting instanceof ColorSetting c) {
					int sw = 10, sg = 3, chromaW = 30;
					int total = ColorSetting.PALETTE.length * (sw + sg) + chromaW;
					int bx = right - total;
					for (int i = 0; i < ColorSetting.PALETTE.length; i++) {
						int sx = bx + i * (sw + sg);
						if (Render2D.inside(mx, my, sx - 1, y + 6, sw + 2, 14)) {
							int alpha = (c.get() >>> 24) & 0xFF;
							if (alpha == 0) alpha = 0xFF;
							c.set((alpha << 24) | (ColorSetting.PALETTE[i] & 0xFFFFFF));
							c.setChroma(false);
						}
					}
					int cxp = bx + ColorSetting.PALETTE.length * (sw + sg);
					if (Render2D.inside(mx, my, cxp, y + 6, chromaW - 2, 14)) c.setChroma(!c.isChroma());
				}
				MaddysClient.saveConfig();
				return true;
			}
			y += ROW_H + 4;
		}
		// reset row
		boolean hudMod = m instanceof HudModule;
		int bw = hudMod ? (cw - 6) / 2 : cw;
		if (Render2D.inside(mx, my, cx, y, bw, ROW_H - 4)) {
			for (Setting<?> st : m.getSettings()) st.reset();
			MaddysClient.saveConfig();
			return true;
		}
		if (hudMod && Render2D.inside(mx, my, cx + bw + 6, y, bw, ROW_H - 4)) {
			((HudModule) m).resetPosition();
			MaddysClient.saveConfig();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (draggingSlider != null) {
			draggingSlider.setPercent((event.x() / s - sliderX) / sliderW);
			return true;
		}
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (draggingSlider != null) {
			draggingSlider = null;
			MaddysClient.saveConfig();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
		targetScroll -= (float) (vertical * 24);
		if (targetScroll < 0) targetScroll = 0;
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int key = event.key();
		if (key == GLFW.GLFW_KEY_ESCAPE && openModule != null) {
			openModule = null;
			targetScroll = scroll = 0;
			return true;
		}
		if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		MaddysClient.saveConfig();
		if (minecraft != null) minecraft.setScreen(parent);
	}

	// ============================================================= helpers

	private static String trim(Font font, String s, int maxW, float scale) {
		if (font.width(s) * scale <= maxW) return s;
		String out = s;
		while (out.length() > 1 && font.width(out + "...") * scale > maxW) out = out.substring(0, out.length() - 1);
		return out + "...";
	}

	private static String[] wrap(Font font, String text, int maxW, int maxLines) {
		List<String> lines = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (String word : text.split(" ")) {
			String test = line.isEmpty() ? word : line + " " + word;
			if (font.width(test) > maxW && !line.isEmpty()) {
				lines.add(line.toString());
				line = new StringBuilder(word);
				if (lines.size() == maxLines) break;
			} else {
				line = new StringBuilder(test);
			}
		}
		if (lines.size() < maxLines && !line.isEmpty()) lines.add(line.toString());
		if (lines.size() == maxLines) {
			String last = lines.get(maxLines - 1);
			String joined = String.join(" ", lines);
			if (joined.length() < text.length()) lines.set(maxLines - 1, trim(font, last + "...", maxW, 1f));
		}
		return lines.toArray(new String[0]);
	}
}
