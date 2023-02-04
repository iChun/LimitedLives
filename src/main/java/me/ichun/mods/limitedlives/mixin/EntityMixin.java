package me.ichun.mods.limitedlives.mixin;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void load(CompoundTag tag, CallbackInfo ci) //load our data before loading additional save data
    {
        if(LimitedLives.eventHandlerServer.isFabricEnv())
        {
            LimitedLives.eventHandlerServer.persistentDataHandler.loadPersistentData((Entity)(Object)this, tag);
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir)
    {
        if(LimitedLives.eventHandlerServer.isFabricEnv())
        {
            LimitedLives.eventHandlerServer.persistentDataHandler.savePersistentData((Entity)(Object)this, tag);
        }
    }
}
