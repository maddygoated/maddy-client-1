package com.maddy.client.module;

import com.maddy.client.module.hud.*;
import com.maddy.client.module.player.AutoRespawn;
import com.maddy.client.module.player.AutoText;
import com.maddy.client.module.player.ToggleSneak;
import com.maddy.client.module.player.ToggleSprint;
import com.maddy.client.module.render.ClearWeather;
import com.maddy.client.module.render.CustomCrosshair;
import com.maddy.client.module.render.DurabilityWarning;
import com.maddy.client.module.render.LowHealthWarning;
import com.maddy.client.module.render.Fullbright;
import com.maddy.client.module.render.HideBossBar;
import com.maddy.client.module.render.HideScoreboard;
import com.maddy.client.module.render.HitMarker;
import com.maddy.client.module.render.MotionBlur;
import com.maddy.client.module.utility.ClientSettings;
import com.maddy.client.module.utility.Zoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		register(new ClientSettings());
		// HUD
		register(new WatermarkHud());
		register(new FpsHud());
		register(new CpsHud());
		register(new PingHud());
		register(new CoordinatesHud());
		register(new DirectionHud());
		register(new SpeedHud());
		register(new KeystrokesHud());
		register(new ArmorStatusHud());
		register(new PotionHud());
		register(new TargetHud());
		register(new ComboHud());
		register(new ReachHud());
		register(new ItemCounterHud());
		register(new ClockHud());
		register(new PlaytimeHud());
		register(new StopwatchHud());
		register(new ServerAddressHud());
		register(new PackDisplayHud());
		register(new MemoryHud());
		register(new SaturationHud());
		register(new DayCounterHud());
		register(new HealthHud());
		register(new BiomeHud());
		register(new LightLevelHud());
		register(new GameTimeHud());
		register(new NearbyPlayersHud());
		register(new BowChargeHud());
		register(new DeathCoordsHud());
		register(new ArrayListHud());
		// Render
		register(new MotionBlur());
		register(new Fullbright());
		register(new CustomCrosshair());
		register(new HitMarker());
		register(new HideScoreboard());
		register(new HideBossBar());
		register(new LowHealthWarning());
		register(new DurabilityWarning());
		register(new ClearWeather());
		// Player
		register(new ToggleSprint());
		register(new ToggleSneak());
		register(new AutoText());
		register(new AutoRespawn());
		// Utility
		register(new Zoom());
	}

	private void register(Module module) {
		modules.add(module);
	}

	public List<Module> all() {
		return Collections.unmodifiableList(modules);
	}

	public List<HudModule> hud() {
		List<HudModule> list = new ArrayList<>();
		for (Module m : modules) if (m instanceof HudModule h) list.add(h);
		return list;
	}

	public List<Module> byCategory(Category category) {
		if (category == null) return all();
		List<Module> list = new ArrayList<>();
		for (Module m : modules) if (m.getCategory() == category) list.add(m);
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T get(Class<T> type) {
		for (Module m : modules) if (type.isInstance(m)) return (T) m;
		throw new IllegalArgumentException("Unknown module " + type.getSimpleName());
	}

	public Module byName(String name) {
		for (Module m : modules) if (m.getName().equalsIgnoreCase(name)) return m;
		return null;
	}

	public void tick() {
		for (Module m : modules) {
			m.onTickAlways();
			if (m.isEnabled()) m.onTick();
		}
	}
}
