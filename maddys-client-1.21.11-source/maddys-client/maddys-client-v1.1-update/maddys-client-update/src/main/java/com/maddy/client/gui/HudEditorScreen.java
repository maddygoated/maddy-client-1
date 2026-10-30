package com.maddy.client.gui;

import com.maddy.client.Branding;
import com.maddy.client.MaddysClient;
import com.maddy.client.module.HudModule;
import com.maddy.client.render.Render2D;
import com.maddy.client.render.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Drag-and-drop HUD layout editor. */
public class HudEditorScreen extends Screen {
	private static final int SNAP = 4;

	private final Screen parent;
	private HudModule dragging;
	private double offX;
	private double offY;
	private boolean snapX;
	private boolean snapY;

	public HudEditorScreen(Screen parent) {
		super(Component.literal(Branding.NAME + " HUD Editor"));
		this.parent = parent;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
		// drawn manually in render()
	}

	private List<HudModule> enabledHud() {
		return MaddysClient.modules().hud().stream().filter(HudModule::isEnabled).toList();
	}

	private HudModule hovered(double mx, double my) {
		List<HudModule> list = enabledHud();
		for (int i = list.size() - 1; i >= 0; i--) {
			HudModule h = list.get(i);
			if (h.screenW > 0 && Render2D.inside(mx, my, h.screenX - 1, h.screenY - 1, h.screenW + 2, h.screenH + 2)) return h;
		}
		return null;
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		g.fill(0, 0, width, height, 0x55000000);

		// grid
		for (int x = 0; x < width; x += 20) g.fill(x, 0, x + 1, height, 0x0CFFFFFF);
		for (int y = 0; y < height; y += 20) g.fill(0, y, width, y + 1, 0x0CFFFFFF);
		g.fill(width / 2, 0, width / 2 + 1, height, snapX ? Theme.ACCENT : 0x22FFFFFF);
		g.fill(0, height / 2, width, height / 2 + 1, snapY ? Theme.ACCENT : 0x22FFFFFF);

		HudModule hover = dragging != null ? dragging : hovered(mouseX, mouseY);
		for (HudModule h : enabledHud()) {
			h.render(g, true);
			boolean active = h == hover;
			int col = active ? Theme.ACCENT : 0x55FFFFFF;
			Render2D.outline(g, h.screenX - 1, h.screenY - 1, h.screenW + 2, h.screenH + 2, col);
		}
		if (hover != null) {
			String label = hover.getName() + "  " + String.format("%.2fx", hover.scale.get());
			int lw = font.width(label) + 8;
			int lx = Math.max(2, Math.min(width - lw - 2, hover.screenX));
			int ly = hover.screenY - 13 < 2 ? hover.screenY + hover.screenH + 3 : hover.screenY - 13;
			Render2D.round(g, lx, ly, lw, 11, 3, Theme.ACCENT);
			g.drawString(font, label, lx + 4, ly + 2, 0xFFFFFFFF, false);
		}

		// top toolbar
		String title = "HUD EDITOR";
		int bw = 80;
		int total = font.width(title) + 16 + bw * 2 + 12;
		int tx = width / 2 - total / 2;
		Render2D.round(g, tx - 6, 6, total + 12, 24, 7, 0xE6111118);
		Render2D.gradientText(g, font, title, tx + 4, 14, Theme.ACCENT, Theme.ACCENT_2, true);
		int b1 = tx + font.width(title) + 16;
		button(g, b1, 9, bw, 18, "Mod Menu", mouseX, mouseY);
		button(g, b1 + bw + 6, 9, bw, 18, "Reset All", mouseX, mouseY);

		String hint = "Drag to move  \u2022  Scroll to resize  \u2022  Right-click to hide  \u2022  ESC to save";
		int hw = font.width(hint) + 12;
		Render2D.round(g, width / 2 - hw / 2, height - 22, hw, 16, 5, 0xCC111118);
		Render2D.centered(g, font, hint, width / 2, height - 18, Theme.MUTED, false);
	}

	private void button(GuiGraphics g, int x, int y, int w, int h, String label, int mx, int my) {
		boolean hover = Render2D.inside(mx, my, x, y, w, h);
		if (hover) Render2D.roundGradientH(g, x, y, w, h, 5, Theme.ACCENT, Theme.ACCENT_2);
		else Render2D.round(g, x, y, w, h, 5, 0xFF262634);
		Render2D.centered(g, font, label, x + w / 2, y + 5, 0xFFFFFFFF, hover);
	}

	private int toolbarButtonX() {
		int bw = 80;
		int total = font.width("HUD EDITOR") + 16 + bw * 2 + 12;
		return width / 2 - total / 2 + font.width("HUD EDITOR") + 16;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mx = event.x();
		double my = event.y();
		int b1 = toolbarButtonX();
		if (Render2D.inside(mx, my, b1, 9, 80, 18)) {
			MaddysClient.saveConfig();
			if (minecraft != null) minecraft.setScreen(new ClickGuiScreen(this));
			return true;
		}
		if (Render2D.inside(mx, my, b1 + 86, 9, 80, 18)) {
			for (HudModule h : MaddysClient.modules().hud()) h.resetPosition();
			MaddysClient.saveConfig();
			return true;
		}
		HudModule h = hovered(mx, my);
		if (h != null) {
			if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				h.setEnabled(false);
				MaddysClient.saveConfig();
			} else {
				dragging = h;
				offX = mx - h.screenX;
				offY = my - h.screenY;
			}
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (dragging == null) return super.mouseDragged(event, dx, dy);
		double nx = event.x() - offX;
		double ny = event.y() - offY;
		int w = dragging.screenW;
		int h = dragging.screenH;
		snapX = snapY = false;

		double centerX = nx + w / 2.0;
		double centerY = ny + h / 2.0;
		if (Math.abs(centerX - width / 2.0) < SNAP) {
			nx = width / 2.0 - w / 2.0;
			snapX = true;
		}
		if (Math.abs(centerY - height / 2.0) < SNAP) {
			ny = height / 2.0 - h / 2.0;
			snapY = true;
		}
		if (nx < SNAP) nx = 2;
		if (ny < SNAP) ny = 2;
		if (nx + w > width - SNAP) nx = width - w - 2;
		if (ny + h > height - SNAP) ny = height - h - 2;

		// snap to other elements' edges
		for (HudModule o : enabledHud()) {
			if (o == dragging || o.screenW == 0) continue;
			if (Math.abs(nx - o.screenX) < SNAP) nx = o.screenX;
			if (Math.abs(ny - (o.screenY + o.screenH + 2)) < SNAP) ny = o.screenY + o.screenH + 2;
			if (Math.abs((ny + h + 2) - o.screenY) < SNAP) ny = o.screenY - h - 2;
		}
		dragging.setPosition(nx / width, ny / height);
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (dragging != null) {
			dragging = null;
			snapX = snapY = false;
			MaddysClient.saveConfig();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
		HudModule h = hovered(mouseX, mouseY);
		if (h != null) {
			h.scale.set(h.scale.get() + (vertical > 0 ? 0.05 : -0.05));
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_R) {
			HudModule h = dragging;
			if (h != null) h.resetPosition();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void onClose() {
		MaddysClient.saveConfig();
		if (minecraft != null) minecraft.setScreen(parent);
	}
}
