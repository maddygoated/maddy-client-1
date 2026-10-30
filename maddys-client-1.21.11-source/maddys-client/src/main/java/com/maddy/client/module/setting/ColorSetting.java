package com.maddy.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.maddy.client.render.Render2D;

public class ColorSetting extends Setting<Integer> {
	public static final int[] PALETTE = {
			0xFFFFFFFF, 0xFFFF4F9A, 0xFF8B5CF6, 0xFF60A5FA, 0xFF22D3EE,
			0xFF4ADE80, 0xFFFACC15, 0xFFFB923C, 0xFFF87171, 0xFF000000
	};

	private boolean chroma;

	public ColorSetting(String name, int defaultValue) {
		super(name, defaultValue);
	}

	public int getColor() {
		return chroma ? Render2D.chroma(0f) : value;
	}

	public int getColor(float chromaOffset) {
		return chroma ? Render2D.chroma(chromaOffset) : value;
	}

	public boolean isChroma() {
		return chroma;
	}

	public void setChroma(boolean chroma) {
		this.chroma = chroma;
	}

	@Override
	public void reset() {
		super.reset();
		chroma = false;
	}

	@Override
	public JsonElement save() {
		JsonObject o = new JsonObject();
		o.addProperty("color", value);
		o.addProperty("chroma", chroma);
		return o;
	}

	@Override
	public void load(JsonElement element) {
		if (element != null && element.isJsonObject()) {
			JsonObject o = element.getAsJsonObject();
			if (o.has("color")) value = o.get("color").getAsInt();
			if (o.has("chroma")) chroma = o.get("chroma").getAsBoolean();
		}
	}
}
