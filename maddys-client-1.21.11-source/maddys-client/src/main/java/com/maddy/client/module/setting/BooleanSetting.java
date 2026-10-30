package com.maddy.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting<Boolean> {
	public float anim;

	public BooleanSetting(String name, boolean defaultValue) {
		super(name, defaultValue);
		this.anim = defaultValue ? 1f : 0f;
	}

	public boolean isOn() {
		return value;
	}

	public void toggle() {
		value = !value;
	}

	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}

	@Override
	public void load(JsonElement element) {
		if (element != null && element.isJsonPrimitive()) value = element.getAsBoolean();
	}
}
