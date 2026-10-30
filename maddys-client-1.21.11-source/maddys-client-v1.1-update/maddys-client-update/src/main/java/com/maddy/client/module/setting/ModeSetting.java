package com.maddy.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
	private final List<String> modes;

	public ModeSetting(String name, String defaultValue, String... modes) {
		super(name, defaultValue);
		this.modes = Arrays.asList(modes);
	}

	public List<String> getModes() {
		return modes;
	}

	public boolean is(String mode) {
		return value.equalsIgnoreCase(mode);
	}

	public void cycle(int direction) {
		int i = modes.indexOf(value);
		if (i < 0) i = 0;
		i = Math.floorMod(i + direction, modes.size());
		value = modes.get(i);
	}

	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}

	@Override
	public void load(JsonElement element) {
		if (element != null && element.isJsonPrimitive()) {
			String s = element.getAsString();
			if (modes.contains(s)) value = s;
		}
	}
}
