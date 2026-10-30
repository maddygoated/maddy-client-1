package com.maddy.client.module.player;

import com.maddy.client.module.Category;
import com.maddy.client.module.Module;
import com.maddy.client.module.setting.ModeSetting;

/** Sends a preset chat message when you press the AutoText key. */
public class AutoText extends Module {
	private final ModeSetting message = add(new ModeSetting("Message", "gg", "gg", "gl hf", "good game!", "wp", "/hub", "/lobby", "/play again"));
	private long lastSent;

	public AutoText() {
		super("AutoText", "Send a preset message with one key (set it in Controls).", Category.PLAYER, true);
	}

	public void send() {
		var p = mc().player;
		if (!isEnabled() || p == null) return;
		long now = System.currentTimeMillis();
		if (now - lastSent < 1500) return; // anti-spam
		lastSent = now;
		String msg = message.get();
		if (msg.startsWith("/")) p.connection.sendCommand(msg.substring(1));
		else p.connection.sendChat(msg);
	}
}
