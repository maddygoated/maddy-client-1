package com.maddy.client.mixin;

import com.maddy.client.Branding;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Sets the game window title to "Maddy's Client 1.21.11". */
@Mixin(Minecraft.class)
public abstract class MinecraftTitleMixin {
	@Inject(method = "createTitle", at = @At("RETURN"), cancellable = true, require = 0)
	private void maddys$title(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(Branding.NAME + " " + Branding.MC_VERSION);
	}
}
