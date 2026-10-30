package com.maddy.client.tracker;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Tracks your own hits: combo, reach and last target (display only). */
public final class CombatTracker {
	private static int combo;
	private static long lastHitTime;
	private static double lastReach = -1;
	private static LivingEntity lastTarget;
	private static int lastHurtTime;

	private CombatTracker() {
	}

	public static void onAttack(Entity target) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		long now = System.currentTimeMillis();

		Vec3 eye = mc.player.getEyePosition();
		AABB box = target.getBoundingBox();
		double cx = Math.max(box.minX, Math.min(eye.x, box.maxX));
		double cy = Math.max(box.minY, Math.min(eye.y, box.maxY));
		double cz = Math.max(box.minZ, Math.min(eye.z, box.maxZ));
		lastReach = Math.sqrt((eye.x - cx) * (eye.x - cx) + (eye.y - cy) * (eye.y - cy) + (eye.z - cz) * (eye.z - cz));

		if (target instanceof LivingEntity living) {
			if (now - lastHitTime > 2000 || living != lastTarget) combo = 0;
			if (living.hurtTime == 0) combo++;
			lastTarget = living;
		}
		lastHitTime = now;
	}

	/** Called every client tick. */
	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			combo = 0;
			lastTarget = null;
			return;
		}
		int hurt = mc.player.hurtTime;
		if (hurt > 0 && lastHurtTime == 0) combo = 0; // we got hit
		lastHurtTime = hurt;
		if (System.currentTimeMillis() - lastHitTime > 2500) combo = 0;
		if (lastTarget != null && (lastTarget.isRemoved() || !lastTarget.isAlive())) lastTarget = null;
	}

	public static int combo() {
		return combo;
	}

	public static double reach() {
		return lastReach;
	}

	public static long lastHitTime() {
		return lastHitTime;
	}

	public static LivingEntity lastTarget() {
		return lastTarget;
	}
}
