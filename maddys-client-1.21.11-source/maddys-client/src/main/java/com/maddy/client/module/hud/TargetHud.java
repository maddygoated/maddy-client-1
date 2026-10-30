package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.render.Render2D;
import com.maddy.client.tracker.CombatTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Shows the name, health and distance of the entity you are fighting. */
public class TargetHud extends HudModule {
	private float shownHealth = 1f;
	private LivingEntity target;

	public TargetHud() {
		super("Target HUD", "Health bar of the player or mob you are fighting.", false, 0.55, 0.55);
	}

	private LivingEntity findTarget() {
		Entity look = mc().crosshairPickEntity;
		if (look instanceof LivingEntity l && l.isAlive()) return l;
		if (System.currentTimeMillis() - CombatTracker.lastHitTime() < 3000) return CombatTracker.lastTarget();
		return null;
	}

	@Override
	protected boolean shouldRender() {
		target = findTarget();
		return target != null;
	}

	@Override
	protected void layout(boolean preview) {
		if (preview) target = findTarget();
		String name = target == null ? "Steve" : target.getName().getString();
		width = Math.max(110, mc().font.width(name) + 50);
		height = 34;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		String name = target == null ? "Steve" : target.getName().getString();
		float hp = target == null ? 14 : target.getHealth();
		float max = target == null ? 20 : Math.max(1, target.getMaxHealth());
		float pct = Render2D.clamp01(hp / max);
		shownHealth = Render2D.approach(shownHealth, pct, 0.15f);

		Render2D.round(g, 4, 4, 26, 26, 4, Render2D.withAlpha(accentColor.getColor(), 0x55));
		Render2D.centered(g, font, name.substring(0, 1).toUpperCase(), 17, 13, 0xFFFFFFFF, true);

		g.drawString(font, name, 35, 6, textColor.getColor(), shadow());
		String info = String.format("%.1f \u2764", hp);
		if (mc().player != null && target != null) info += String.format("  %.1fm", mc().player.distanceTo(target));
		Render2D.scaledText(g, font, info, 35, 16, 0.7f, Render2D.alpha(textColor.getColor(), 0.85f), false);

		int bx = 35, by = 25, bw = width - 40, bh = 4;
		Render2D.round(g, bx, by, bw, bh, 2, 0x55000000);
		Render2D.round(g, bx, by, Math.max(2, (int) (bw * shownHealth)), bh, 2, Render2D.healthColor(pct));
	}
}
