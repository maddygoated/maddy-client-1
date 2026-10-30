package com.maddy.client.module;

import com.maddy.client.module.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {
	private final String name;
	private final String description;
	private final Category category;
	private final boolean enabledByDefault;
	private final List<Setting<?>> settings = new ArrayList<>();
	private boolean enabled;

	/** UI animation state (0 = off, 1 = on). */
	public float toggleAnim;
	public float hoverAnim;

	protected Module(String name, String description, Category category, boolean enabledByDefault) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.enabledByDefault = enabledByDefault;
		this.enabled = enabledByDefault;
		this.toggleAnim = enabledByDefault ? 1f : 0f;
	}

	protected static Minecraft mc() {
		return Minecraft.getInstance();
	}

	protected <S extends Setting<?>> S add(S setting) {
		settings.add(setting);
		return setting;
	}

	public List<Setting<?>> getSettings() {
		return Collections.unmodifiableList(settings);
	}

	public void setEnabled(boolean value) {
		if (value == enabled) return;
		enabled = value;
		if (value) onEnable();
		else onDisable();
	}

	/** Restores state from config without firing enable/disable side effects twice. */
	public void loadEnabled(boolean value) {
		if (value == enabled) return;
		setEnabled(value);
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	/** Short text drawn inside the module's icon tile. */
	public String getGlyph() {
		String[] parts = name.split(" ");
		if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
		return name.substring(0, Math.min(2, name.length())).toUpperCase();
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	/** Called every client tick while enabled. */
	public void onTick() {
	}

	/** Called every client tick regardless of state (for keybind driven modules). */
	public void onTickAlways() {
	}

	/** Called when the game is closing. */
	public void onShutdown() {
	}
}
