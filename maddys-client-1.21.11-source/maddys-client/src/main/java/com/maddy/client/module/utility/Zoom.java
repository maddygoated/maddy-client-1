package com.maddy.client.module.utility;

import com.maddy.client.mixin.OptionInstanceAccessor;
import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.BooleanSetting;
import com.maddy.client.module.setting.NumberSetting;

/** Hold the zoom key (default C) to zoom in. */
public class Zoom extends Module {
	private final NumberSetting factor = add(new NumberSetting("Zoom Factor", 4, 2, 10, 0.5, "x"));
	private final BooleanSetting smooth = add(new BooleanSetting("Smooth Camera", true));

	private Integer originalFov;
	private boolean originalSmooth;
	private boolean zooming;

	public Zoom() {
		super("Zoom", "Hold the zoom key (default C) to zoom in.", Category.UTILITY, true);
	}

	public void update(boolean keyDown) {
		var mc = mc();
		if (mc.options == null) return;
		boolean want = keyDown && isEnabled() && mc.player != null && mc.screen == null;
		if (want && !zooming) {
			originalFov = mc.options.fov().get();
			originalSmooth = mc.options.smoothCamera;
			int fov = Math.max(1, (int) Math.round(originalFov / factor.get()));
			((OptionInstanceAccessor) (Object) mc.options.fov()).maddys$setValue(fov);
			if (smooth.isOn()) mc.options.smoothCamera = true;
			zooming = true;
		} else if (!want && zooming) {
			stop();
		}
	}

	private void stop() {
		var mc = mc();
		if (originalFov != null) ((OptionInstanceAccessor) (Object) mc.options.fov()).maddys$setValue(originalFov);
		mc.options.smoothCamera = originalSmooth;
		originalFov = null;
		zooming = false;
	}

	@Override
	public void onDisable() {
		if (zooming) stop();
	}

	@Override
	public void onShutdown() {
		if (zooming) stop();
	}
}
