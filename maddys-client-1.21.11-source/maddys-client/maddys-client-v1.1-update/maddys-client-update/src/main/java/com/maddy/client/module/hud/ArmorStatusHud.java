package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ModeSetting;
import com.maddy.client.render.Render2D;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ArmorStatusHud extends HudModule {
	private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private final ModeSetting durability = add(new ModeSetting("Durability", "Value", "Value", "Percent", "Value / Max", "None"));
	private final BooleanSetting showHand = add(new BooleanSetting("Show Held Item", true));
	private final BooleanSetting horizontal = add(new BooleanSetting("Horizontal", false));

	private final List<ItemStack> items = new ArrayList<>();

	public ArmorStatusHud() {
		super("Armor Status", "Shows your armor and held item with durability.", true, 0.93, 0.40);
	}

	@Override
	protected void layout(boolean preview) {
		items.clear();
		var p = mc().player;
		if (p == null || preview && isEmptyArmor()) {
			items.add(new ItemStack(Items.DIAMOND_HELMET));
			items.add(new ItemStack(Items.DIAMOND_CHESTPLATE));
			items.add(new ItemStack(Items.DIAMOND_LEGGINGS));
			items.add(new ItemStack(Items.DIAMOND_BOOTS));
			if (showHand.isOn()) items.add(new ItemStack(Items.DIAMOND_SWORD));
		} else {
			for (EquipmentSlot s : SLOTS) {
				ItemStack st = p.getItemBySlot(s);
				if (!st.isEmpty()) items.add(st);
			}
			if (showHand.isOn() && !p.getMainHandItem().isEmpty()) items.add(p.getMainHandItem());
		}
		Font font = mc().font;
		if (horizontal.isOn()) {
			width = Math.max(20, items.size() * 20 + 4);
			height = durability.is("None") ? 20 : 28;
		} else {
			int tw = 0;
			for (ItemStack st : items) tw = Math.max(tw, font.width(text(st)));
			width = 22 + (durability.is("None") ? 0 : tw + 4);
			height = Math.max(20, items.size() * 18 + 4);
		}
	}

	private boolean isEmptyArmor() {
		var p = mc().player;
		if (p == null) return true;
		for (EquipmentSlot s : SLOTS) if (!p.getItemBySlot(s).isEmpty()) return false;
		return true;
	}

	private String text(ItemStack st) {
		if (durability.is("None")) return "";
		if (!st.isDamageableItem()) return st.getCount() > 1 ? String.valueOf(st.getCount()) : "";
		int max = st.getMaxDamage();
		int left = max - st.getDamageValue();
		return switch (durability.get()) {
			case "Percent" -> (max == 0 ? 100 : left * 100 / max) + "%";
			case "Value / Max" -> left + "/" + max;
			default -> String.valueOf(left);
		};
	}

	private int color(ItemStack st) {
		if (!st.isDamageableItem() || st.getMaxDamage() == 0) return textColor.getColor();
		return Render2D.healthColor(1f - (float) st.getDamageValue() / st.getMaxDamage());
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		drawBackground(g, 0, 0, width, height);
		if (horizontal.isOn()) {
			int x = 2;
			for (ItemStack st : items) {
				g.renderItem(st, x + 2, 2);
				String t = text(st);
				if (!t.isEmpty()) Render2D.scaledText(g, font, t, x + 10 - font.width(t) * 0.35f, 20, 0.7f, color(st), shadow());
				x += 20;
			}
		} else {
			int y = 2;
			for (ItemStack st : items) {
				g.renderItem(st, 3, y + 1);
				String t = text(st);
				if (!t.isEmpty()) g.drawString(font, t, 22, y + 5, color(st), shadow());
				y += 18;
			}
		}
	}
}
