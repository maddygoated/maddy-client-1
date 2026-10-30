package com.maddy.client.module.render;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.NumberSetting;
import in.spunkyinsaan.motionblurplus.BlurConfigStore;
import in.spunkyinsaan.motionblurplus.MotionBlurPlusClient;

/** Frame-blending motion blur, powered by the bundled MotionBlur Plus engine (MIT, by spunkyinsaan). */
public class MotionBlur extends Module {
	private final NumberSetting strength = add(new NumberSetting("Strength", 40, 1, 98, 1, "%"));

	public MotionBlur() {
		super("Motion Blur", "Smooth frame-blending blur when you move your camera.", Category.RENDER, true);
	}

	private void sync() {
		try {
			if (BlurConfigStore.isEnabled() != isEnabled() || BlurConfigStore.getStrength() != strength.getInt()) {
				MotionBlurPlusClient.applySettings(isEnabled(), strength.getInt());
			}
		} catch (Throwable ignored) {
			// engine not loaded yet
		}
	}

	@Override
	public void onEnable() {
		sync();
	}

	@Override
	public void onDisable() {
		sync();
	}

	@Override
	public void onTickAlways() {
		sync();
	}
}
