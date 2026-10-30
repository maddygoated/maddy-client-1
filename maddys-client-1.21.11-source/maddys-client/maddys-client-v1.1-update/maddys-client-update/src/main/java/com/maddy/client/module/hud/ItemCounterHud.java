package com.maddy.client.module.hud;

import com.maddy.client.module.HudModule;
import com.maddy.client.module.setting.BooleanSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Counts important PvP items in your inventory. */
public class ItemCounterHud extends HudModule {
	private final BooleanSetting hideZero = add(new BooleanSetting("Hide Empty", true));
	private static final Item[] TRACKED = {Items.ARROW, Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE, Items.ENDER_PEARL, Items.EXPERIENCE_BOTTLE};
	private final List<ItemStack> icons = new ArrayList<>();
	private final List<Integer> counts = new ArrayList<>();

	public ItemCounterHud() {
		super("Item Counter", "Counts arrows, totems, gapples, pearls and XP bottles.", false, 0.93, 0.70);
	}

	@Override
	protected void layout(boolean preview) {
		icons.clear();
		counts.clear();
		var p = mc().player;
		for (Item item : TRACKED) {
			int count = 0;
			if (p != null) {
				var inv = p.getInventory();
				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack st = inv.getItem(i);
					if (st.is(item)) count += st.getCount();
				}
			} else {
				count = 16;
			}
			if (count == 0 && hideZero.isOn() && !preview) continue;
			icons.add(new ItemStack(item));
			counts.add(count);
		}
		Font font = mc().font;
		int tw = 0;
		for (int c : counts) tw = Math.max(tw, font.width(String.valueOf(c)));
		width = 24 + tw + 4;
		height = Math.max(18, icons.size() * 18 + 2);
	}

	@Override
	protected boolean shouldRender() {
		return mc().player != null;
	}

	@Override
	protected void draw(GuiGraphics g, Font font, boolean preview) {
		if (icons.isEmpty()) return;
		drawBackground(g, 0, 0, width, height);
		int y = 1;
		for (int i = 0; i < icons.size(); i++) {
			g.renderItem(icons.get(i), 3, y + 1);
			g.drawString(font, String.valueOf(counts.get(i)), 23, y + 5, counts.get(i) == 0 ? 0xFFF87171 : textColor.getColor(), shadow());
			y += 18;
		}
	}
}
