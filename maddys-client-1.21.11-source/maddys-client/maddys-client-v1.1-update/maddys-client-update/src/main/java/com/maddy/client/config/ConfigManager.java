package com.maddy.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maddy.client.MaddysClient;
import com.maddy.client.module.HudModule;
import com.maddy.client.module.Module;
import com.maddy.client.module.ModuleManager;
import com.maddy.client.module.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves every module, setting and HUD position to config/maddys-client.json. */
public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final Path file = FabricLoader.getInstance().getConfigDir().resolve("maddys-client.json");

	public void load(ModuleManager modules) {
		if (!Files.exists(file)) return;
		try {
			JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
			JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();
			for (Module m : modules.all()) {
				if (!mods.has(m.getName())) continue;
				JsonObject o = mods.getAsJsonObject(m.getName());
				if (o.has("settings")) {
					JsonObject s = o.getAsJsonObject("settings");
					for (Setting<?> setting : m.getSettings()) {
						JsonElement e = s.get(setting.getName());
						if (e != null) {
							try {
								setting.load(e);
							} catch (Exception ignored) {
								// keep default on bad value
							}
						}
					}
				}
				if (m instanceof HudModule h && o.has("x") && o.has("y")) {
					h.setPosition(o.get("x").getAsDouble(), o.get("y").getAsDouble());
				}
				if (o.has("enabled")) {
					m.loadEnabled(o.get("enabled").getAsBoolean());
					m.toggleAnim = m.isEnabled() ? 1f : 0f;
				}
			}
		} catch (Exception e) {
			MaddysClient.LOGGER.error("Could not read {}", file, e);
		}
	}

	public void save(ModuleManager modules) {
		JsonObject root = new JsonObject();
		root.addProperty("version", 1);
		JsonObject mods = new JsonObject();
		for (Module m : modules.all()) {
			JsonObject o = new JsonObject();
			o.addProperty("enabled", m.isEnabled());
			if (m instanceof HudModule h) {
				o.addProperty("x", h.getX());
				o.addProperty("y", h.getY());
			}
			JsonObject s = new JsonObject();
			for (Setting<?> setting : m.getSettings()) s.add(setting.getName(), setting.save());
			o.add("settings", s);
			mods.add(m.getName(), o);
		}
		root.add("modules", mods);
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(root), StandardCharsets.UTF_8);
		} catch (IOException e) {
			MaddysClient.LOGGER.error("Could not save {}", file, e);
		}
	}
}
