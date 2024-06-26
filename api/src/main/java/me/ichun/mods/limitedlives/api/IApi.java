package me.ichun.mods.limitedlives.api;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface IApi
{
    default void setPlayerDeaths(@NotNull ServerPlayer player, int deaths, boolean resetHealth){}
    default int getPlayerDeaths(@NotNull ServerPlayer player) { return -1; }
}
