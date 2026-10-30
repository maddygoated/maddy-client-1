package com.maddy.client.module.utility;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.ModeSetting;
import com.maddy.client.module.setting.NumberSetting;

/** Global client options. Always on - cannot be turned off. */
public class ClientSettings extends Module {
	public final NumberSetting hudScale = add(new NumberSetting("HUD Scale", 0.75, 0.5, 1.5, 0.05, "x"));
	public final BooleanSetting crispText = add(new BooleanSetting("Crisp Text", true));
	public final ModeSetting menuSize = add(new ModeSetting("Menu Size", "Small", "Small", "Normal", "Large"));
	public final BooleanSetting menuAnimations = add(new BooleanSetting("Menu Animations", true));

	public ClientSettings() {
		super("Client Settings", "HUD size, crisp text and menu size for Maddy's Client.", Category.UTILITY, true);
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(true);
	}

	@Override
	public String getGlyph() {
		return "CS";
	}
}
