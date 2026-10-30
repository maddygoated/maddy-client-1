package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BowItem;

/** Shows how far your bow is drawn so you release at full power. */
public class BowChargeHud extends HudModule {
	private float pct;

	public BowChargeHud() {
		super("Bow Charge", "Shows bow draw power - release at 100%.", false, 0.46, 0.58);
	}

	@Override
	protected boolean shouldRender() {
		var p = mc().player;
		return p != null && p.isUsingItem() && p.getUseItem().getItem() instanceof BowItem;
	}

	@Override
	protected void layout(boolean preview) {
		width = 70;
		height = 18;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		var p = mc().player;
		float power = (p != null && p.isUsingItem()) ? BowItem.getPowerForTime(p.getTicksUsingItem()) : 0.7f;
		pct = Render2D.approach(pct, power, 0.5f);
		drawBackground(g, 0, 0, width, height);
		boolean full = power >= 1f;
		String t = full ? "FULL" : Math.round(power * 100) + "%";
		g.drawString(font, t, 5, 3, full ? 0xFF4ADE80 : textColor.getColor(), shadow());
		Render2D.round(g, 5, 13, width - 10, 2, 1, 0x55000000);
		Render2D.round(g, 5, 13, Math.max(2, (int) ((width - 10) * pct)), 2, 1, full ? 0xFF4ADE80 : accentColor.getColor());
	}
}
