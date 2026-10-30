package com.maddy.client.tracker;

import net.minecraft.client.Minecraft;

/** Tracks how long you have been in the current world / server. */
public final class SessionTracker {
	private static long joinedAt = -1;

	private SessionTracker() {
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			joinedAt = -1;
		} else if (joinedAt < 0) {
			joinedAt = System.currentTimeMillis();
		}
	}

	public static long millis() {
		return joinedAt < 0 ? 0 : System.currentTimeMillis() - joinedAt;
	}

	public static String format(long ms) {
		long s = ms / 1000;
		long h = s / 3600;
		long m = (s % 3600) / 60;
		long sec = s % 60;
		return h > 0 ? String.format("%d:%02d:%02d", h, m, sec) : String.format("%02d:%02d", m, sec);
	}
}
