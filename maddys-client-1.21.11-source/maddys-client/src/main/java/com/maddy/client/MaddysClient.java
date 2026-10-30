package com.maddy.client;

import com.maddy.client.config.ConfigManager;
import com.maddy.client.gui.ClickGuiScreen;
import com.maddy.client.gui.HudEditorScreen;
import com.maddy.client.module.HudModule;
import com.maddy.client.module.Module;
import com.maddy.client.module.ModuleManager;
import com.maddy.client.module.hud.PotionHud;
import com.maddy.client.module.hud.StopwatchHud;
import com.maddy.client.module.player.AutoText;
import com.maddy.client.module.render.CustomCrosshair;
import com.maddy.client.module.render.Fullbright;
import com.maddy.client.module.render.HideBossBar;
import com.maddy.client.module.render.HideScoreboard;
import com.maddy.client.module.render.HitMarker;
import com.maddy.client.module.utility.Zoom;
import com.maddy.client.render.Render2D;
import com.maddy.client.render.Theme;
import com.maddy.client.tracker.CombatTracker;
import com.maddy.client.tracker.CpsTracker;
import com.maddy.client.tracker.SessionTracker;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaddysClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Branding.NAME);

	private static ModuleManager modules;
	private static ConfigManager config;

	private static KeyMapping menuKey;
	private static KeyMapping hudEditorKey;
	private static KeyMapping zoomKey;
	private static KeyMapping fullbrightKey;
	private static KeyMapping stopwatchKey;
	private static KeyMapping autoTextKey;

	public static ModuleManager modules() {
		return modules;
	}

	public static void saveConfig() {
		if (config != null && modules != null) config.save(modules);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Branding.MOD_ID, path);
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("Loading {} v{} for Minecraft {}", Branding.NAME, Branding.VERSION, Branding.MC_VERSION);
		modules = new ModuleManager();
		config = new ConfigManager();
		config.load(modules);

		registerKeys();
		registerHud();
		registerEvents();
		registerTitleScreen();
	}

	// ------------------------------------------------------------------ keys

	private static void registerKeys() {
		KeyMapping.Category cat = KeyMapping.Category.register(id("main"));
		menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.maddys-client.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
		hudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.maddys-client.hud_editor", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
		zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.maddys-client.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, cat));
		fullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.maddys-client.fullbright", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
		stopwatchKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.maddys-client.stopwatch", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
		autoTextKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.maddys-client.autotext", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
	}

	// ------------------------------------------------------------------- HUD

	private static void registerHud() {
		HudElementRegistry.addLast(id("hud"), (GuiGraphics g, net.minecraft.client.DeltaTracker delta) -> {
			Minecraft mc = Minecraft.getInstance();
			CpsTracker.update();
			if (mc.options.hideGui) return;
			if (mc.screen instanceof HudEditorScreen) return;
			for (HudModule h : modules.hud()) {
				if (h.isEnabled()) h.render(g, false);
			}
		});

		HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, vanilla -> (g, delta) -> {
			Minecraft mc = Minecraft.getInstance();
			CustomCrosshair cross = modules.get(CustomCrosshair.class);
			boolean firstPerson = mc.options.getCameraType().isFirstPerson();
			if (cross.isEnabled() && firstPerson) {
				cross.render(g);
			} else {
				vanilla.render(g, delta);
			}
			HitMarker marker = modules.get(HitMarker.class);
			if (marker.isEnabled() && firstPerson) marker.render(g);
		});

		HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD, vanilla -> (g, delta) -> {
			if (!modules.get(HideScoreboard.class).isEnabled()) vanilla.render(g, delta);
		});

		HudElementRegistry.replaceElement(VanillaHudElements.BOSS_BAR, vanilla -> (g, delta) -> {
			if (!modules.get(HideBossBar.class).isEnabled()) vanilla.render(g, delta);
		});

		HudElementRegistry.replaceElement(VanillaHudElements.STATUS_EFFECTS, vanilla -> (g, delta) -> {
			PotionHud potions = modules.get(PotionHud.class);
			if (!(potions.isEnabled() && potions.hideVanilla.isOn())) vanilla.render(g, delta);
		});
	}

	// ---------------------------------------------------------------- events

	private static void registerEvents() {
		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			SessionTracker.tick();
			CombatTracker.tick();
			modules.tick();

			modules.get(Zoom.class).update(zoomKey.isDown());

			while (menuKey.consumeClick()) {
				if (mc.screen == null) mc.setScreen(new ClickGuiScreen(null));
			}
			while (hudEditorKey.consumeClick()) {
				if (mc.screen == null && mc.level != null) mc.setScreen(new HudEditorScreen(null));
			}
			while (fullbrightKey.consumeClick()) {
				modules.get(Fullbright.class).toggle();
				saveConfig();
			}
			while (stopwatchKey.consumeClick()) {
				modules.get(StopwatchHud.class).startStop();
			}
			while (autoTextKey.consumeClick()) {
				modules.get(AutoText.class).send();
			}
		});

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (level.isClientSide()) CombatTracker.onAttack(entity);
			return InteractionResult.PASS;
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
			for (Module m : modules.all()) m.onShutdown();
			saveConfig();
		});
	}

	// --------------------------------------------------------- title screen

	private static void registerTitleScreen() {
		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
			if (!(screen instanceof TitleScreen)) return;
			Screens.getButtons(screen).add(Button.builder(Component.literal("\u2726 " + Branding.NAME), b -> client.setScreen(new ClickGuiScreen(screen)))
					.bounds(width - 112, 6, 106, 20)
					.build());
			ScreenEvents.afterRender(screen).register((s, g, mouseX, mouseY, delta) -> {
				var font = client.font;
				Render2D.round(g, 6, 6, 8 + font.width(Branding.NAME_UPPER) + 10, 20, 5, 0x99000000);
				Render2D.roundGradientH(g, 10, 12, 8, 8, 2, Theme.ACCENT, Theme.ACCENT_2);
				Render2D.gradientText(g, font, Branding.NAME_UPPER, 22, 12, Theme.ACCENT, Theme.ACCENT_2, true);
				g.drawString(font, "v" + Branding.VERSION + "  \u2022  " + Branding.MC_VERSION, 8, 30, 0xCCFFFFFF, true);
			});
		});
	}
}
