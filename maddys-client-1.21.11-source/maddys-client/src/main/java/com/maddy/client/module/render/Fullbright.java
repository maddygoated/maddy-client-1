package com.maddy.client.module.render;

import com.maddy.client.mixin.OptionInstanceAccessor;
import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.NumberSetting;

/** Raises brightness far beyond the vanilla slider. */
public class Fullbright extends Module {
	private final NumberSetting strength = add(new NumberSetting("Strength", 15, 1, 15, 0.5));
	private Double original;

	public Fullbright() {
		super("Fullbright", "See clearly in caves and at night.", Category.RENDER, false);
	}

	@Override
	public void onEnable() {
		if (mc().options == null) return;
		if (original == null) original = mc().options.gamma().get();
		apply();
	}

	@Override
	public void onTick() {
		apply();
	}

	private void apply() {
		if (mc().options == null) return;
		if (original == null) original = mc().options.gamma().get();
		Double target = strength.get();
		if (!target.equals(mc().options.gamma().get())) {
			((OptionInstanceAccessor) (Object) mc().options.gamma()).maddys$setValue(target);
		}
	}

	@Override
	public void onDisable() {
		restore();
	}

	@Override
	public void onShutdown() {
		restore();
	}

	private void restore() {
		if (mc().options == null) return;
		double back = original == null ? 0.5 : Math.max(0, Math.min(1, original));
		((OptionInstanceAccessor) (Object) mc().options.gamma()).maddys$setValue(back);
		original = null;
	}
}
