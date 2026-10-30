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
import com.maddy.client.module.utility.ClientSettings;
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
import java.util.Locale;

/** The Maddy's Client mod menu (Right Shift). */
public class ClickGuiScreen extends Screen {
	private static final int W = 500;
	private static final int H = 292;
	private static final int SIDEBAR = 112;
	private static final int COLS = 3;
	private static final int CARD_H = 54;
	private static final int GAP = 7;
	private static final int ROW_H = 24;
	private static final int SEARCH_W = 140;

	private static Category lastCategory = null;

	private final Screen parent;
	private Category category = lastCategory;
	private Module openModule;
	private String search = "";
	private boolean searchFocused;
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

	private static ClientSettings settings() {
		return MaddysClient.modules().get(ClientSettings.class);
	}

	@Override
	protected void init() {
		ClientSettings cs = settings();
		float frac = switch (cs.menuSize.get()) {
			case "Normal" -> 0.80f;
			case "Large" -> 0.94f;
			default -> 0.66f;
		};
		if (cs.crispText.isOn()) {
			double gui = minecraft.getWindow().getGuiScale();
			double pixels = Math.floor(Math.min(width * gui * frac / W, height * gui * frac / H));
			s = (float) (Math.max(1, pixels) / gui);
		} else {
			s = Math.min(width * frac / W, height * frac / H);
		}
		px = (int) ((width / s - W) / 2);
		py = (int) ((height / s - H) / 2);
		if (!cs.menuAnimations.isOn()) openAnim = 1f;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
		// drawn manually in render()
	}

	/** Sharp small text scale. */
	private float small(float f) {
		return settings().crispText.isOn() ? Render2D.crisp(f, s) : f;
	}

	// ================================================================ render

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		openAnim = Render2D.approach(openAnim, 1f, 0.2f);
		scroll = Render2D.approach(scroll, targetScroll, 0.3f);

		g.fill(0, 0, width, height, Render2D.alpha(Theme.BACKDROP, openAnim));

		double mx = mouseX / s;
		double my = mouseY / s;
		Font font = this.font;

		g.pose().pushMatrix();
		g.pose().scale(s, s);
		if (openAnim < 0.999f) {
			float pop = 0.95f + 0.05f * openAnim;
			g.pose().translate(px + W / 2f, py + H / 2f);
			g.pose().scale(pop, pop);
			g.pose().translate(-(px + W / 2f), -(py + H / 2f));
		}

		Render2D.round(g, px - 2, py - 2, W + 4, H + 4, 12, 0x30000000);
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
		Render2D.roundGradientH(g, px + 12, py + 12, 22, 22, 6, Theme.ACCENT, Theme.ACCENT_2);
		Render2D.centered(g, font, "M", px + 23, py + 19, 0xFFFFFFFF, true);
		Render2D.gradientText(g, font, "MADDY'S", px + 40, py + 13, Theme.ACCENT, Theme.ACCENT_2, true);
		Render2D.scaledText(g, font, "CLIENT " + Branding.MC_VERSION, px + 40, py + 24, small(0.7f), Theme.MUTED, false);

		int y = py + 48;
		for (int i = 0; i <= Category.values().length; i++) {
			Category c = i == 0 ? null : Category.values()[i - 1];
			String label = c == null ? "All Mods" : c.display();
			String glyph = c == null ? "*" : c.glyph();
			boolean selected = category == c && search.isEmpty();
			boolean hover = Render2D.inside(mx, my, px + 8, y, SIDEBAR - 16, 21);
			categoryAnim[i] = Render2D.approach(categoryAnim[i], selected ? 1f : (hover ? 0.45f : 0f), 0.25f);
			float a = categoryAnim[i];
			if (a > 0.01f) Render2D.round(g, px + 8, y, SIDEBAR - 16, 21, 6, Render2D.withAlpha(Theme.ACCENT, (int) (0x30 * a)));
			if (selected) Render2D.round(g, px + 8, y + 5, 3, 11, 1, Theme.ACCENT);
			Render2D.round(g, px + 15, y + 4, 13, 13, 4, selected ? Theme.ACCENT : 0xFF262634);
			Render2D.centered(g, font, glyph, px + 22, y + 7, 0xFFFFFFFF, false);
			g.drawString(font, label, px + 33, y + 7, selected ? Theme.TEXT : Render2D.lerpColor(Theme.MUTED, Theme.TEXT, a), false);
			int count = 0;
			for (Module m : MaddysClient.modules().byCategory(c)) if (m.isEnabled() && !(m instanceof ClientSettings)) count++;
			String cnt = String.valueOf(count);
			g.drawString(font, cnt, px + SIDEBAR - 14 - font.width(cnt), y + 7, Theme.DIM, false);
			y += 24;
		}

		// settings (gear) row
		int sy = py + H - 80;
		boolean gearHover = Render2D.inside(mx, my, px + 8, sy, SIDEBAR - 16, 21);
		boolean inSettings = openModule instanceof ClientSettings;
		if (gearHover || inSettings) Render2D.round(g, px + 8, sy, SIDEBAR - 16, 21, 6, Render2D.withAlpha(Theme.ACCENT, inSettings ? 0x30 : 0x18));
		Render2D.gearIcon(g, px + 22, sy + 10, inSettings ? Theme.ACCENT : Theme.MUTED);
		g.drawString(font, "Settings", px + 33, sy + 7, inSettings ? Theme.TEXT : Theme.MUTED, false);

		boolean canEdit = minecraft != null && minecraft.level != null;
		int bx = px + 10, by = py + H - 52, bw = SIDEBAR - 20, bh = 20;
		boolean hover = canEdit && Render2D.inside(mx, my, bx, by, bw, bh);
		if (canEdit) Render2D.roundGradientH(g, bx, by, bw, bh, 6, hover ? 0xFFFF6BAE : Theme.ACCENT, hover ? 0xFFA07BFF : Theme.ACCENT_2);
		else Render2D.round(g, bx, by, bw, bh, 6, 0xFF262634);
		Render2D.centered(g, font, "Edit HUD", bx + bw / 2, by + 6, canEdit ? 0xFFFFFFFF : Theme.DIM, canEdit);

		Render2D.scaledText(g, font, "v" + Branding.VERSION + "  \u2022  Right Shift", px + 12, py + H - 22, small(0.7f), Theme.DIM, false);
	}

	private int contentX() {
		return px + SIDEBAR + 12;
	}

	private int contentY() {
		return py + 42;
	}

	private int contentW() {
		return W - SIDEBAR - 24;
	}

	private int contentH() {
		return H - 42 - 10;
	}

	private List<Module> visibleModules() {
		List<Module> list = new ArrayList<>();
		String q = search.trim().toLowerCase(Locale.ROOT);
		for (Module m : MaddysClient.modules().all()) {
			if (m instanceof ClientSettings) continue;
			if (!q.isEmpty()) {
				if (m.getName().toLowerCase(Locale.ROOT).contains(q) || m.getDescription().toLowerCase(Locale.ROOT).contains(q)
						|| m.getCategory().display().toLowerCase(Locale.ROOT).contains(q)) list.add(m);
			} else if (category == null || m.getCategory() == category) {
				list.add(m);
			}
		}
		return list;
	}

	private void drawSearch(GuiGraphics g, Font font, double mx, double my) {
		int x = px + W - 12 - SEARCH_W, y = py + 12, h = 18;
		boolean hover = Render2D.inside(mx, my, x, y, SEARCH_W, h);
		Render2D.round(g, x, y, SEARCH_W, h, 6, searchFocused ? 0xFF2A2236 : (hover ? 0xFF232330 : 0xFF1B1B25));
		if (searchFocused) Render2D.round(g, x + 6, y + h - 1, SEARCH_W - 12, 1, 0, Theme.ACCENT);
		Render2D.searchIcon(g, x + 10, y + 9, searchFocused ? Theme.ACCENT : Theme.MUTED);
		String text = search.isEmpty() ? "Search mods..." : search;
		int color = search.isEmpty() ? Theme.DIM : Theme.TEXT;
		String shown = text;
		while (font.width(shown) > SEARCH_W - 30 && shown.length() > 1) shown = shown.substring(1);
		g.drawString(font, shown, x + 20, y + 5, color, false);
		if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
			int cx = x + 20 + (search.isEmpty() ? 0 : font.width(shown)) + 1;
			Render2D.rect(g, cx, y + 4, 1, 10, Theme.ACCENT);
		}
		if (!search.isEmpty()) {
			boolean xHover = Render2D.inside(mx, my, x + SEARCH_W - 14, y, 14, h);
			g.drawString(font, "x", x + SEARCH_W - 11, y + 5, xHover ? Theme.ACCENT : Theme.MUTED, false);
		}
	}

	private void drawHeader(GuiGraphics g, Font font, String title, String subtitle, boolean back, double mx, double my) {
		int x = contentX();
		if (back) {
			boolean hover = Render2D.inside(mx, my, x, py + 12, 18, 18);
			Render2D.round(g, x, py + 12, 18, 18, 5, hover ? Theme.CARD_HOVER : Theme.CARD);
			Render2D.centered(g, font, "<", x + 9, py + 17, 0xFFFFFFFF, false);
			x += 24;
		}
		g.drawString(font, title, x, py + 12, Theme.TEXT, true);
		Render2D.scaledText(g, font, subtitle, x, py + 24, small(0.75f), Theme.MUTED, false);
	}

	private void drawModules(GuiGraphics g, Font font, double mx, double my) {
		List<Module> list = visibleModules();
		int enabled = 0;
		for (Module m : list) if (m.isEnabled()) enabled++;
		String title = !search.isEmpty() ? "Search" : (category == null ? "All Mods" : category.display());
		drawHeader(g, font, title, list.size() + " mods  \u2022  " + enabled + " on  \u2022  right-click = settings", false, mx, my);
		drawSearch(g, font, mx, my);

		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		int cardW = (cw - GAP * (COLS - 1)) / COLS;
		int rows = (list.size() + COLS - 1) / COLS;
		int total = Math.max(0, rows * (CARD_H + GAP) - GAP);
		clampScroll(total, ch);

		if (list.isEmpty()) {
			Render2D.centered(g, font, "No mods match \"" + search + "\"", cx + cw / 2, cy + 40, Theme.MUTED, false);
			return;
		}

		g.enableScissor(cx - 2, cy, cx + cw + 2, cy + ch);
		for (int i = 0; i < list.size(); i++) {
			Module m = list.get(i);
			int x = cx + (i % COLS) * (cardW + GAP);
			int y = cy + (i / COLS) * (CARD_H + GAP) - Math.round(scroll);
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
		if (m.hoverAnim > 0.01f) Render2D.round(g, x, y, w, 1, 0, Render2D.withAlpha(Theme.ACCENT, (int) (0x60 * m.hoverAnim)));
		if (m.toggleAnim > 0.01f) Render2D.round(g, x + 6, y + CARD_H - 2, w - 12, 2, 1, Render2D.alpha(Theme.ACCENT, m.toggleAnim));

		if (m.toggleAnim > 0.5f) Render2D.roundGradientH(g, x + 7, y + 7, 20, 20, 5, Theme.ACCENT, Theme.ACCENT_2);
		else Render2D.round(g, x + 7, y + 7, 20, 20, 5, 0xFF2E2E3E);
		Render2D.centered(g, font, m.getGlyph(), x + 17, y + 13, 0xFFFFFFFF, true);

		drawSwitch(g, x + w - 29, y + 11, m.toggleAnim);

		g.drawString(font, trim(font, m.getName(), w - 14, 1f), x + 7, y + 31, Theme.TEXT, false);
		float ds = small(0.7f);
		String desc = trim(font, m.getDescription(), (int) ((w - 26) / ds), 1f);
		Render2D.scaledText(g, font, desc, x + 7, y + 42, ds, Theme.MUTED, false);

		if (!m.getSettings().isEmpty()) {
			boolean dotsHover = Render2D.inside(mx, my, x + w - 18, y + CARD_H - 14, 14, 12);
			int dc = dotsHover ? Theme.ACCENT : Theme.DIM;
			for (int i = 0; i < 3; i++) Render2D.rect(g, x + w - 15 + i * 4, y + CARD_H - 8, 2, 2, dc);
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
		boolean client = m instanceof ClientSettings;
		drawHeader(g, font, m.getName(), client ? m.getDescription() : m.getCategory().display() + "  \u2022  " + m.getDescription(), true, mx, my);

		if (!client) {
			m.toggleAnim = Render2D.approach(m.toggleAnim, m.isEnabled() ? 1f : 0f, 0.25f);
			drawSwitch(g, contentX() + contentW() - 26, py + 15, m.toggleAnim);
		}

		int cx = contentX(), cy = contentY(), cw = contentW(), ch = contentH();
		List<Setting<?>> settingsList = visibleSettings(m);
		int total = (settingsList.size() + 1) * (ROW_H + 4);
		clampScroll(total, ch);

		g.enableScissor(cx - 2, cy, cx + cw + 2, cy + ch);
		int y = cy - Math.round(scroll);
		for (Setting<?> setting : settingsList) {
			drawSettingRow(g, font, setting, cx, y, cw, mx, my);
			y += ROW_H + 4;
		}
		boolean hudMod = m instanceof HudModule;
		int bw = hudMod ? (cw - 6) / 2 : cw;
		boolean h1 = Render2D.inside(mx, my, cx, y, bw, ROW_H - 4);
		Render2D.round(g, cx, y, bw, ROW_H - 4, 6, h1 ? 0xFF3A2030 : 0xFF2A1A24);
		Render2D.centered(g, font, "Reset Settings", cx + bw / 2, y + 6, 0xFFFF8FB8, false);
		if (hudMod) {
			boolean h2 = Render2D.inside(mx, my, cx + bw + 6, y, bw, ROW_H - 4);
			Render2D.round(g, cx + bw + 6, y, bw, ROW_H - 4, 6, h2 ? Theme.CARD_HOVER : Theme.CARD);
			Render2D.centered(g, font, "Reset Position", cx + bw + 6 + bw / 2, y + 6, Theme.TEXT, false);
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
		g.drawString(font, setting.getName(), x + 9, y + 8, Theme.TEXT, false);
		int right = x + w - 9;

		if (setting instanceof BooleanSetting b) {
			b.anim = Render2D.approach(b.anim, b.isOn() ? 1f : 0f, 0.25f);
			drawSwitch(g, right - 22, y + 6, b.anim);
		} else if (setting instanceof NumberSetting n) {
			int bw = 110;
			int bx = right - bw;
			int by = y + 11;
			Render2D.round(g, bx, by, bw, 3, 1, 0xFF33334A);
			int fill = (int) (bw * n.percent());
			Render2D.roundGradientH(g, bx, by, Math.max(2, fill), 3, 1, Theme.ACCENT, Theme.ACCENT_2);
			Render2D.circle(g, bx + fill, by + 1, 4, 0xFFFFFFFF);
			String v = n.display();
			g.drawString(font, v, bx - 8 - font.width(v), y + 8, Theme.MUTED, false);
		} else if (setting instanceof ModeSetting mode) {
			String v = mode.get();
			int pw = font.width(v) + 26;
			int bx = right - pw;
			boolean hover = Render2D.inside(mx, my, bx, y + 4, pw, 16);
			Render2D.round(g, bx, y + 4, pw, 16, 5, hover ? 0xFF34344A : 0xFF2A2A3C);
			g.drawString(font, "<", bx + 5, y + 8, Theme.ACCENT, false);
			Render2D.centered(g, font, v, bx + pw / 2, y + 8, Theme.TEXT, false);
			g.drawString(font, ">", bx + pw - 10, y + 8, Theme.ACCENT, false);
		} else if (setting instanceof ColorSetting c) {
			int sw = 10, sg = 3, chromaW = 30;
			int total = ColorSetting.PALETTE.length * (sw + sg) + chromaW;
			int bx = right - total;
			for (int i = 0; i < ColorSetting.PALETTE.length; i++) {
				int col = ColorSetting.PALETTE[i];
				int sx = bx + i * (sw + sg);
				boolean selected = !c.isChroma() && (c.get() & 0xFFFFFF) == (col & 0xFFFFFF);
				if (selected) Render2D.round(g, sx - 2, y + 5, sw + 4, sw + 4, 3, 0xFFFFFFFF);
				Render2D.round(g, sx, y + 7, sw, sw, 3, col | 0xFF000000);
			}
			int cxp = bx + ColorSetting.PALETTE.length * (sw + sg);
			if (c.isChroma()) Render2D.roundGradientH(g, cxp, y + 5, chromaW - 2, 14, 4, Render2D.chroma(0f), Render2D.chroma(0.5f));
			else Render2D.round(g, cxp, y + 5, chromaW - 2, 14, 4, 0xFF2A2A3C);
			Render2D.centered(g, font, "RGB", cxp + (chromaW - 2) / 2, y + 8, 0xFFFFFFFF, c.isChroma());
			Render2D.round(g, bx - 16, y + 7, 10, 10, 3, c.getColor());
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

		// search box
		int sx = px + W - 12 - SEARCH_W, sy = py + 12;
		if (openModule == null && Render2D.inside(mx, my, sx, sy, SEARCH_W, 18)) {
			if (!search.isEmpty() && mx >= sx + SEARCH_W - 14) {
				search = "";
				targetScroll = scroll = 0;
			}
			searchFocused = true;
			return true;
		}
		searchFocused = false;

		int y = py + 48;
		for (int i = 0; i <= Category.values().length; i++) {
			if (Render2D.inside(mx, my, px + 8, y, SIDEBAR - 16, 21)) {
				category = i == 0 ? null : Category.values()[i - 1];
				lastCategory = category;
				openModule = null;
				search = "";
				targetScroll = scroll = 0;
				return true;
			}
			y += 24;
		}

		if (Render2D.inside(mx, my, px + 8, py + H - 80, SIDEBAR - 16, 21)) {
			openModule = settings();
			targetScroll = scroll = 0;
			return true;
		}

		if (minecraft != null && minecraft.level != null && Render2D.inside(mx, my, px + 10, py + H - 52, SIDEBAR - 20, 20)) {
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
		int cardW = (cw - GAP * (COLS - 1)) / COLS;
		for (int i = 0; i < list.size(); i++) {
			Module m = list.get(i);
			int x = cx + (i % COLS) * (cardW + GAP);
			int y = cy + (i / COLS) * (CARD_H + GAP) - Math.round(scroll);
			if (!Render2D.inside(mx, my, x, y, cardW, CARD_H)) continue;
			boolean dots = Render2D.inside(mx, my, x + cardW - 18, y + CARD_H - 14, 14, 12);
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
		if (Render2D.inside(mx, my, contentX(), py + 12, 18, 18)) {
			openModule = null;
			targetScroll = scroll = 0;
			return true;
		}
		if (!(m instanceof ClientSettings) && Render2D.inside(mx, my, contentX() + contentW() - 26, py + 15, 22, 12)) {
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
						int sxp = bx + i * (sw + sg);
						if (Render2D.inside(mx, my, sxp - 1, y + 5, sw + 2, 14)) {
							int alpha = (c.get() >>> 24) & 0xFF;
							if (alpha == 0) alpha = 0xFF;
							c.set((alpha << 24) | (ColorSetting.PALETTE[i] & 0xFFFFFF));
							c.setChroma(false);
						}
					}
					int cxp = bx + ColorSetting.PALETTE.length * (sw + sg);
					if (Render2D.inside(mx, my, cxp, y + 5, chromaW - 2, 14)) c.setChroma(!c.isChroma());
				}
				MaddysClient.saveConfig();
				if (m instanceof ClientSettings) init();
				return true;
			}
			y += ROW_H + 4;
		}
		boolean hudMod = m instanceof HudModule;
		int bw = hudMod ? (cw - 6) / 2 : cw;
		if (Render2D.inside(mx, my, cx, y, bw, ROW_H - 4)) {
			for (Setting<?> st : m.getSettings()) st.reset();
			MaddysClient.saveConfig();
			if (m instanceof ClientSettings) init();
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
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			if (openModule != null) {
				openModule = null;
				targetScroll = scroll = 0;
				return true;
			}
			if (!search.isEmpty()) {
				search = "";
				searchFocused = false;
				targetScroll = scroll = 0;
				return true;
			}
		}
		if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
			onClose();
			return true;
		}
		if (openModule == null) {
			if (key == GLFW.GLFW_KEY_BACKSPACE) {
				if (!search.isEmpty()) search = search.substring(0, search.length() - 1);
				targetScroll = scroll = 0;
				return true;
			}
			char ch = 0;
			if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) ch = (char) ('a' + (key - GLFW.GLFW_KEY_A));
			else if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) ch = (char) ('0' + (key - GLFW.GLFW_KEY_0));
			else if (key == GLFW.GLFW_KEY_SPACE && !search.isEmpty()) ch = ' ';
			if (ch != 0 && search.length() < 24) {
				search += ch;
				searchFocused = true;
				targetScroll = scroll = 0;
				return true;
			}
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
}
