package com.maddy.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting<Double> {
	private final double min;
	private final double max;
	private final double step;
	private final String suffix;

	public NumberSetting(String name, double defaultValue, double min, double max, double step) {
		this(name, defaultValue, min, max, step, "");
	}

	public NumberSetting(String name, double defaultValue, double min, double max, double step, String suffix) {
		super(name, defaultValue);
		this.min = min;
		this.max = max;
		this.step = step;
		this.suffix = suffix;
	}

	@Override
	public void set(Double v) {
		double clamped = Math.max(min, Math.min(max, v));
		double stepped = Math.round(clamped / step) * step;
		this.value = Math.max(min, Math.min(max, stepped));
	}

	public float getFloat() {
		return value.floatValue();
	}

	public int getInt() {
		return (int) Math.round(value);
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double percent() {
		return (value - min) / (max - min);
	}

	public void setPercent(double pct) {
		set(min + (max - min) * Math.max(0, Math.min(1, pct)));
	}

	public String display() {
		String num = step >= 1 ? String.valueOf((long) Math.round(value)) : String.format("%.2f", value);
		return num + suffix;
	}

	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}

	@Override
	public void load(JsonElement element) {
		if (element != null && element.isJsonPrimitive()) set(element.getAsDouble());
	}
}
