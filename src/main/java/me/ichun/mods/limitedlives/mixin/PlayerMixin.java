package me.ichun.mods.limitedlives.mixin;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci)
    {
        LimitedLives.eventHandlerServer.firePlayerTickEndEvent(((Player)(Object)this));
    }
}
