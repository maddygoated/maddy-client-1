package com.maddy.client.module.render;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.NumberSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** Warns you before your armor or weapon breaks. */
public class DurabilityWarning extends Module {
	private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
	private final NumberSetting percent = add(new NumberSetting("Warn Below", 10, 3, 30, 1, "%"));

	public DurabilityWarning() {
		super("Durability Warning", "Flashing alert before your armor or tools break.", Category.RENDER, true);
	}

	public void render(GuiGraphics g) {
		var p = mc().player;
		if (p == null) return;
		ItemStack worst = null;
		float worstPct = 1f;
		for (EquipmentSlot slot : SLOTS) {
			ItemStack st = p.getItemBySlot(slot);
			if (st.isEmpty() || !st.isDamageableItem() || st.getMaxDamage() <= 0) continue;
			float pct = 1f - (float) st.getDamageValue() / st.getMaxDamage();
			if (pct < worstPct) {
				worstPct = pct;
				worst = st;
			}
		}
		if (worst == null || worstPct * 100 > percent.get()) return;
		if ((System.currentTimeMillis() / 400) % 2 == 0) return;
		Font font = mc().font;
		String text = "\u26a0 " + worst.getHoverName().getString() + " " + Math.round(worstPct * 100) + "% left!";
		int w = font.width(text) + 26;
		int x = g.guiWidth() / 2 - w / 2;
		int y = g.guiHeight() / 2 + 18;
		Render2D.round(g, x, y, w, 20, 5, 0xCC2A0A12);
		g.renderItem(worst, x + 3, y + 2);
		g.drawString(font, text, x + 22, y + 6, 0xFFF87171, true);
	}
}
